package com.example.apigatewayservice.filter;

import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.env.Environment;
//import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.*;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

// 왜 추상 팩토리 메서드 패턴을 썼을까? -> Configurable + GatewayFilterFactory 를 구현하고 있기 때문.
// Configurable -> 환경설정을 위해?
// <AuthorizationHeaderFilter.Config>는 FilterFactory가 super클래스의 멤버인 ConfigClass에 저장해준다.
// 즉, AuthorizationHeaderFilter.Config를 ConfigClass로 해서, GatewayFilter를 생성해준다. 이후 Filter는 bean에 등록, 사용가능
@Component
@Slf4j
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config> {
    // public final Environment env; 생성자가 생겨서 안먹힘
    Environment env;

    public AuthorizationHeaderFilter(Environment env){
        super(Config.class); // 생성자 안에서도 상위 클래스의 생성자는 가장 먼저 위치해 있어야 한다.
        this.env = env;
    }

    // AuthorizationHeaderFilter.Config를 이용해서 GatewayFilter를 만든다.
    @Override
    public GatewayFilter apply(Config config) {
        // exchange는 webflux ServerWebExchange에 들어있는 Request와 Reponse이다..
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            //request 헤더에 authorization 헤더가 있는지 확인.
            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)){
                //HTTP Status 코드 반환..
                return onError(exchange, "No authorization header", HttpStatus.UNAUTHORIZED);
            }
            //authorization 헤더가 있으면, 값을 가져와서 저장
            String authorizationHeader = request.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
            //Bearer 부분을 삭제
            String jwt =authorizationHeader.replace("Bearer", "");

            if(!isJwtValid(jwt)){
                return onError(exchange,"JWT token is not valid", HttpStatus.UNAUTHORIZED);
            }

            //람다식의 리턴
            return chain.filter(exchange);
        };
    }


    // Mono나 flux는 spring webflux의 데이터 단위, dispatcher를 쓰는 방향과는 사뭇 다르다.
    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);

        log.error(err);
        return response.setComplete();
    }

    //jwt가 정상적인지 확인하는 알고리즘.
    private boolean isJwtValid(String jwt) {
        boolean returnValue = true;
        // jwt의 sub 값을 저장할 변수..
        String subject = null;

        try {
            subject = Jwts.parser().setSigningKey(env.getProperty("token.secret")) // 시크릿 키를 가져와서
                    .parseClaimsJws(jwt).getBody()  //jwt를 분석하고 바디안에 들어 있는 값을 가져온다.
                    .getSubject(); // 바디안에 subject를 가져온다..
        } catch (Exception ex){
            returnValue = false;
        }

        if (subject == null || subject.isEmpty()){
            returnValue = false;
        }

        return returnValue;
    }

    public static class Config {

    }

}
