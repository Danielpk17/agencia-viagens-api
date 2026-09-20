package com.senai.agenciaviagens.security;

import com.senai.agenciaviagens.domain.Perfil;
import com.senai.agenciaviagens.domain.Usuario;
import com.senai.agenciaviagens.repository.UsuarioRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario nao encontrado: " + username));

        List<SimpleGrantedAuthority> autoridades = usuario.getPerfis().stream()
                .map(Perfil::getNome)
                .map(SimpleGrantedAuthority::new)
                .toList();

        return User.withUsername(usuario.getUsername())
                .password(usuario.getSenha())
                .disabled(!usuario.isAtivo())
                .authorities(autoridades)
                .build();
    }
}
