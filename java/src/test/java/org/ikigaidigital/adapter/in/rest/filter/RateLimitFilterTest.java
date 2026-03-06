package org.ikigaidigital.adapter.in.rest.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.any;

@ExtendWith(MockitoExtension.class)
class RateLimitFilterTest {

    private RateLimitFilter filter;

    @Mock
    private FilterChain filterChain;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        filter = new RateLimitFilter(3, 60);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @Test
    void shouldAllowRequestsWithinLimit() throws ServletException, IOException {
        filter.doFilterInternal(request, response, filterChain);

        assertThat(response.getStatus()).isEqualTo(200);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldRejectRequestsExceedingLimit() throws ServletException, IOException {
        for (int i = 0; i < 3; i++) {
            MockHttpServletResponse resp = new MockHttpServletResponse();
            filter.doFilterInternal(request, resp, filterChain);
        }

        filter.doFilterInternal(request, response, filterChain);

        assertThat(response.getStatus()).isEqualTo(429);
        assertThat(response.getHeader("Retry-After")).isEqualTo("60");
        verify(filterChain, times(3)).doFilter(any(), any());
    }

    @Test
    void shouldReturnJsonBodyOn429() throws ServletException, IOException {
        for (int i = 0; i < 3; i++) {
            filter.doFilterInternal(request, new MockHttpServletResponse(), filterChain);
        }

        filter.doFilterInternal(request, response, filterChain);

        assertThat(response.getContentAsString()).contains("Rate limit exceeded");
        assertThat(response.getContentType()).isEqualTo("application/json");
    }

    @Test
    void shouldTrackDifferentIpsSeparately() throws ServletException, IOException {
        for (int i = 0; i < 3; i++) {
            filter.doFilterInternal(request, new MockHttpServletResponse(), filterChain);
        }

        MockHttpServletRequest otherRequest = new MockHttpServletRequest();
        otherRequest.setRemoteAddr("192.168.1.100");
        MockHttpServletResponse otherResponse = new MockHttpServletResponse();

        filter.doFilterInternal(otherRequest, otherResponse, filterChain);

        assertThat(otherResponse.getStatus()).isEqualTo(200);
        verify(filterChain, times(4)).doFilter(any(), any());
    }
}
