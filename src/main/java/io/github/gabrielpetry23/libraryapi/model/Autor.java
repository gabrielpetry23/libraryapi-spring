package io.github.gabrielpetry23.libraryapi.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "autor", schema = "public") // Definir o nome da tabela e o schema, se tiver apenas um schema não precisa (padrão public)
@Data // agrupa @Getter @Setter @EqualsAndHashCode @RequiredArgsConstructor
@ToString(exclude = {"livros"}) // não vai mostrar os livros no toString
@EntityListeners(AuditingEntityListener.class) // para auditar as datas de cadastro e atualizacao
public class Autor {

    //@Column não é obrigatório pois @Entity ja faz com que os atributos sejam mapeados como Colunas

    @Id //Primary Key
    @Column(name = "id") // Nome como esta no banco, se for igual não precisa
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "nome", length = 100, nullable = false) //varchar(100) , not null
    private String nome;

    @Column(name = "data_nascimento", nullable = false)
    private LocalDate dataNascimento;

    @Column(name = "nacionalidade", length = 50, nullable = false)
    private String nacionalidade;

    @OneToMany(mappedBy = "autor",
    //cascade = CascadeType.ALL,
    fetch = FetchType.LAZY) // Lazy carrega os livros apenas quando necessário
    private List<Livro> livros;

    @CreatedDate // Cria automaticamente
    @Column(name = "data_cadastro")
    private LocalDateTime dataCadastro;

    @LastModifiedDate // Atualiza automaticamente
    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;
}
