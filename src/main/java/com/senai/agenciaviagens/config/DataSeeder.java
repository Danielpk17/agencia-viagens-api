package com.senai.agenciaviagens.config;

import com.senai.agenciaviagens.domain.Destino;
import com.senai.agenciaviagens.domain.Perfil;
import com.senai.agenciaviagens.domain.Usuario;
import com.senai.agenciaviagens.repository.DestinoRepository;
import com.senai.agenciaviagens.repository.PerfilRepository;
import com.senai.agenciaviagens.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Component
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true", matchIfMissing = true)
public class DataSeeder implements CommandLineRunner {

    private static final Logger LOG = LoggerFactory.getLogger(DataSeeder.class);

    private final PerfilRepository perfilRepository;
    private final UsuarioRepository usuarioRepository;
    private final DestinoRepository destinoRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.seed.admin-username}")
    private String adminUsername;

    @Value("${app.seed.admin-password}")
    private String adminPassword;

    @Value("${app.seed.user-username}")
    private String userUsername;

    @Value("${app.seed.user-password}")
    private String userPassword;

    public DataSeeder(PerfilRepository perfilRepository,
                      UsuarioRepository usuarioRepository,
                      DestinoRepository destinoRepository,
                      PasswordEncoder passwordEncoder) {
        this.perfilRepository = perfilRepository;
        this.usuarioRepository = usuarioRepository;
        this.destinoRepository = destinoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        Perfil perfilAdmin = obterOuCriarPerfil(Perfil.ROLE_ADMIN);
        Perfil perfilUser = obterOuCriarPerfil(Perfil.ROLE_USER);

        criarUsuarioSeNecessario(adminUsername, adminPassword, "Administrador da Agencia",
                "admin@agenciaviagens.com", Set.of(perfilAdmin, perfilUser));
        criarUsuarioSeNecessario(userUsername, userPassword, "Maria Cliente",
                "maria@agenciaviagens.com", Set.of(perfilUser));

        criarDestinosSeNecessario();
    }

    private Perfil obterOuCriarPerfil(String nome) {
        return perfilRepository.findByNome(nome)
                .orElseGet(() -> perfilRepository.save(new Perfil(nome)));
    }

    private void criarUsuarioSeNecessario(String username, String senha, String nome,
                                          String email, Set<Perfil> perfis) {
        if (usuarioRepository.existsByUsername(username)) {
            return;
        }
        Usuario usuario = new Usuario();
        usuario.setUsername(username);
        usuario.setSenha(passwordEncoder.encode(senha));
        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setAtivo(true);
        usuario.setPerfis(perfis);
        usuarioRepository.save(usuario);
        LOG.info("Usuario de teste criado: {}", username);
    }

    private void criarDestinosSeNecessario() {
        if (destinoRepository.count() > 0) {
            return;
        }
        List<Destino> destinos = List.of(
                montarDestino("Fernando de Noronha", "Brasil", "Fernando de Noronha",
                        "Arquipelago com praias preservadas e mergulho em aguas cristalinas",
                        new BigDecimal("7890.00"), 6),
                montarDestino("Gramado e Canela", "Brasil", "Gramado",
                        "Roteiro na serra gaucha com gastronomia, parques tematicos e clima europeu",
                        new BigDecimal("3250.50"), 4),
                montarDestino("Bariloche", "Argentina", "San Carlos de Bariloche",
                        "Destino de montanha com lagos, esqui no inverno e trilhas no verao",
                        new BigDecimal("5400.00"), 7),
                montarDestino("Lisboa Historica", "Portugal", "Lisboa",
                        "Bairros historicos, miradouros e passeio de bonde pela cidade",
                        new BigDecimal("9600.00"), 8));
        destinoRepository.saveAll(destinos);
        LOG.info("Destinos iniciais carregados no banco de dados: {}", destinos.size());
    }

    private Destino montarDestino(String nome, String pais, String cidade, String descricao,
                                  BigDecimal preco, Integer dias) {
        Destino destino = new Destino();
        destino.setNome(nome);
        destino.setPais(pais);
        destino.setCidade(cidade);
        destino.setDescricao(descricao);
        destino.setPrecoMedio(preco);
        destino.setDuracaoDias(dias);
        destino.setAtivo(true);
        return destino;
    }
}
