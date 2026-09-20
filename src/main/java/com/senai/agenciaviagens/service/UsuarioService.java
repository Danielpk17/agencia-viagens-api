package com.senai.agenciaviagens.service;

import com.senai.agenciaviagens.domain.Perfil;
import com.senai.agenciaviagens.domain.Usuario;
import com.senai.agenciaviagens.dto.UsuarioRequest;
import com.senai.agenciaviagens.dto.UsuarioResponse;
import com.senai.agenciaviagens.exception.RecursoNaoEncontradoException;
import com.senai.agenciaviagens.exception.RegraNegocioException;
import com.senai.agenciaviagens.mapper.UsuarioMapper;
import com.senai.agenciaviagens.repository.PerfilRepository;
import com.senai.agenciaviagens.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PerfilRepository perfilRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioMapper usuarioMapper;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          PerfilRepository perfilRepository,
                          PasswordEncoder passwordEncoder,
                          UsuarioMapper usuarioMapper) {
        this.usuarioRepository = usuarioRepository;
        this.perfilRepository = perfilRepository;
        this.passwordEncoder = passwordEncoder;
        this.usuarioMapper = usuarioMapper;
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listar() {
        return usuarioRepository.findAllByOrderByUsernameAsc().stream()
                .map(usuarioMapper::paraResposta)
                .toList();
    }

    @Transactional(readOnly = true)
    public UsuarioResponse buscarPorId(Long id) {
        return usuarioMapper.paraResposta(obterEntidade(id));
    }

    @Transactional(readOnly = true)
    public UsuarioResponse buscarPorUsername(String username) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuario nao encontrado: " + username));
        return usuarioMapper.paraResposta(usuario);
    }

    @Transactional
    public UsuarioResponse criar(UsuarioRequest requisicao) {
        String username = requisicao.username().trim().toLowerCase();
        if (usuarioRepository.existsByUsername(username)) {
            throw new RegraNegocioException("Ja existe um usuario cadastrado com o username " + username);
        }
        Usuario usuario = new Usuario();
        usuario.setUsername(username);
        usuario.setNome(requisicao.nome().trim());
        usuario.setEmail(requisicao.email());
        usuario.setSenha(passwordEncoder.encode(requisicao.senha()));
        usuario.setAtivo(true);
        usuario.setPerfis(resolverPerfis(requisicao.perfis()));
        return usuarioMapper.paraResposta(usuarioRepository.save(usuario));
    }

    @Transactional
    public UsuarioResponse alterarStatus(Long id, boolean ativo) {
        Usuario usuario = obterEntidade(id);
        usuario.setAtivo(ativo);
        return usuarioMapper.paraResposta(usuarioRepository.save(usuario));
    }

    @Transactional
    public void excluir(Long id) {
        Usuario usuario = obterEntidade(id);
        usuarioRepository.delete(usuario);
    }

    private Usuario obterEntidade(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuario nao encontrado para o id " + id));
    }

    private Set<Perfil> resolverPerfis(Set<String> nomes) {
        Set<Perfil> perfis = new HashSet<>();
        for (String nome : nomes) {
            String nomeNormalizado = nome.trim().toUpperCase();
            if (!nomeNormalizado.startsWith("ROLE_")) {
                nomeNormalizado = "ROLE_" + nomeNormalizado;
            }
            String nomeFinal = nomeNormalizado;
            Perfil perfil = perfilRepository.findByNome(nomeFinal)
                    .orElseThrow(() -> new RegraNegocioException("Perfil invalido: " + nomeFinal
                            + ". Perfis disponiveis: ADMIN e USER"));
            perfis.add(perfil);
        }
        return perfis;
    }
}
