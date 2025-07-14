package com.cryptoadz.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

	
	
    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    // 1) define o PasswordEncoder
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 2) provedor de autenticação baseado em banco
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider prov = new DaoAuthenticationProvider();
        prov.setUserDetailsService(customUserDetailsService);
        prov.setPasswordEncoder(passwordEncoder());
        return prov;
    }

    // 3) expor o AuthenticationManager (para Login)
    @Bean
    public AuthenticationManager authManager(AuthenticationConfiguration cfg) throws Exception {
        return cfg.getAuthenticationManager();
    }

  /** @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
            "https://cryptoadz-production.up.railway.app",
            "http://localhost:8080"
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
  }*/
   
    
    // 4) definir as regras de segurança e injetar o provedor
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
      
          //  .cors(cors -> cors.configurationSource(corsConfigurationSource())) // aqui habilita o CORS e indica a configuração
            .authenticationProvider(authenticationProvider())
            .authorizeHttpRequests(auth -> auth
            		.requestMatchers(
            			    "/login",
            			    "/cadastro",
            			    "/css/**",
            			    "/js/**",
            			    "/usuarios/quantidade",
            			    "/api/anuncio/quantidade",
            			    "/api/anuncio/quantidade-cliks",
            			    "/api/visualizacoes/status-bloqueio",
            			    "/api/anuncio/cadastrar",
            			    "/api/visualizacoes/tokens-creditados/**",
            			    "/api/visualizacoes/registrar-visualizacao/**",
            			    "/api/anuncio/meus-anuncios/**",
            			    "/missoes/incrementar-assistir/**",
            			    "/missoes/incrementar-cadastro/**",
            			    "/missoes/reivindicar-cadastro/**",
            			    "/missoes/reivindicar-assistir/",
            			    "/missoes/status/**",
            			    "/api/avisos/**",
            			    "/api/bonus/coletar/**",
            			    "/api/bonus/verificar/**",
            			    "/api/banners/bannser",
            			    "/api/visualizacoes/banner/registrar/**",
                            "/api/visualizacoes/banner/coletar/**",
                            "/api/visualizacoes/banner/status/**",
                            "/api/depositos/fazer/**",
                            "/api/depositos/status/**",
                            "/api/depositos/historico/**"
                         
                        
            			   
            			    
            			    
            			    
            			).permitAll()


            		
            		    .requestMatchers("/api/visualizacoes/**").permitAll()
            		    .requestMatchers("/api/saldo").authenticated()
            		    .requestMatchers("/api/saldoUsdt").authenticated()
            		    .anyRequest().authenticated()
            		    
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/dashboard", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .sessionManagement(session -> session
                .invalidSessionUrl("/login?session=expired")
                .maximumSessions(1)
                .expiredUrl("/login?expired")
            )
            
          

            
            .csrf().disable();

        return http.build();
    }

    

 
}





