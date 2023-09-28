package com.booklink.backend.config;


import com.booklink.backend.dto.user.CreateUserDto;
import com.booklink.backend.model.Forum;
import com.booklink.backend.model.Tag;
import com.booklink.backend.model.User;
import com.booklink.backend.repository.ForumRepository;
import com.booklink.backend.repository.TagRepository;
import com.booklink.backend.repository.UserRepository;
import com.booklink.backend.service.UserService;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component("InitializationConfig")
@Transactional
@Generated
public class InitializationConfig implements CommandLineRunner {
    @Autowired
    UserRepository userRepository;
    @Autowired
    ForumRepository forumRepository;
    @Autowired
    TagRepository tagRepository;
    @Autowired
    UserService userService;


    final Logger logger = LoggerFactory.getLogger(InitializationConfig.class);


    @Override
    public void run(String... args) throws Exception {
        logger.info("Users initialization begin");
        this.loadUsers();
        logger.info("Forums initialization begin");
        this.loadForums();
        logger.info("DB data intialized");
    }

    private void loadUsers(){
        CreateUserDto user1 = CreateUserDto.builder().username("lucia21").email("lucia_21@gmail.com").password("Tros177jo9").build();
        CreateUserDto user2 = CreateUserDto.builder().username("tomas").email("tomas@gmail.com").password("Tomas123").build();
        CreateUserDto user3 = CreateUserDto.builder().username("sofiagarcia1").email("sofiagarcia@yahoo.com").password("Puc3tmmm").build();
        CreateUserDto user4 = CreateUserDto.builder().username("julianlopez").email("julian_lopez@outlook.com").password("50Tru1b").build();
        CreateUserDto user5 = CreateUserDto.builder().username("anamartinez").email("ana_martinez@icloud.com").password("Goo3df1z").build();
        CreateUserDto user6 = CreateUserDto.builder().username("diegos1").email("diego.s@bing.com").password("Mifuelrbx1").build();
        CreateUserDto user8 = CreateUserDto.builder().username("laurag").email("laura.g@protonmail.com").password("Vel14for").build();
        CreateUserDto user9 = CreateUserDto.builder().username("pablofernandez").email("pablo_fernandez@zoho.com").password("Ruuuu123").build();
        CreateUserDto user10 = CreateUserDto.builder().username("mariarivera").email("maria.rivera@aol.com").password("Lhii1769").build();
        userService.registerUser(user1);
        userService.registerUser(user2);
        userService.registerUser(user3);
        userService.registerUser(user4);
        userService.registerUser(user5);
        userService.registerUser(user6);
        userService.registerUser(user8);
        userService.registerUser(user9);
        userService.registerUser(user10);
    }


    private void loadForums(){

        User user1 = userRepository.findByUsername("tomas").get();
        User user2 = userRepository.findByUsername("laurag").get();
        User user3 = userRepository.findByUsername("sofiagarcia1").get();
        User user4 = userRepository.findByUsername("pablofernandez").get();
        User user5 = userRepository.findByUsername("mariarivera").get();

        ArrayList<User> users1 = new ArrayList<>();
        users1.add(user2);
        users1.add(user4);

        ArrayList<User> users2 = new ArrayList<>();
        users2.add(user1);

        ArrayList<User> users3 = new ArrayList<>();
        users3.add(user5);
        users3.add(user4);
        users3.add(user2);

        ArrayList<User> users4 = new ArrayList<>();
        users4.add(user1);
        users4.add(user2);
        users4.add(user5);
        users4.add(user3);

        ArrayList<User> users5 = new ArrayList<>();
        users5.add(user3);
        users5.add(user4);

        Forum forum1 = Forum.builder()
                .name("Harry Potter")
                .userId(user1.getId())
                .description("Bienvenidos al foro de los fanáticos de Harry Potter, la mejor saga de libros jamás escrita. Aquí podrás compartir tus opiniones, teorías, curiosidades y experiencias sobre el maravilloso mundo mágico creado por J.K. Rowling. Este foro es un espacio abierto y respetuoso para todos los que aman esta saga, sin importar su edad, género, nacionalidad o casa de Hogwarts. ¡Únete a la comunidad y disfruta de la magia!")
                .img("https://images.hola.com/imagenes/actualidad/20210901195369/harry-potter-curiosidades-pelicula-20-aniversario-nf/0-989-980/harry-t.jpg")
                .members(users1)
                .membersAmount(users1.size()+1)
                .build();
        Forum forum2 = Forum.builder()
                .name("Narnia")
                .userId(user2.getId())
                .description("¿Te fascina el mundo de Narnia y sus increíbles personajes? Entonces este foro es para ti. Aquí podrás discutir todo tipo de temas relacionados con esta obra maestra de C.S. Lewis, desde debates sobre los reyes, teorías conspirativas, análisis literarios, comparaciones con otras sagas y mucho más. Este foro es un lugar de encuentro para todos los que quieren explorar más a fondo el universo de Narnia y sus secretos. ¡Atrévete a entrar por el ropero y vive la aventura!")
                .img("https://e00-elmundo.uecdn.es/assets/multimedia/imagenes/2018/10/08/15390123619440.jpg")
                .members(users2)
                .membersAmount(users2.size()+1)
                .build();
        Forum forum3 = Forum.builder()
                .name("1984")
                .userId(user3.getId())
                .description("Si te gusta 1984, el libro de Orwell que predijo el totalitarismo, este foro es para ti. Aquí podrás discutir sobre el libro y su relación con el mundo actual. ¿Qué tan cerca estamos del Gran Hermano, la neolengua y la policía del pensamiento? Este foro es un lugar de reflexión y crítica para los que quieren comprender 1984 y su impacto en nuestra sociedad. ¡Sé parte de la resistencia y defiende la libertad!")
                .img("https://www.storytel.com/images/640x640/0002933941.jpg")
                .members(users3)
                .membersAmount(users3.size()+1)
                .build();
        Forum forum4 = Forum.builder()
                .name("Simbolismos de Ana Frank")
                .userId(user4.getId())
                .description("Este foro es para los que se emocionan con el diario de Ana Frank, una niña judía que vivió el nazismo. Aquí podrás discutir los simbolismos de esta obra, que expresa los sentimientos de Ana y su familia. También podrás aprender cómo el diario de Ana Frank es un símbolo de resistencia y humanidad. Este foro es un espacio de homenaje y conocimiento para los que quieren conocer más sobre Ana Frank y su aporte a la historia y la literatura. ¡Disfruta del diario más famoso del mundo y comparte tus ideas!")
                .img("https://sbslibreria.vteximg.com.br/arquivos/ids/215579-1000-1000/9789878354194.jpg")
                .members(users4)
                .membersAmount(3)
                .build();
        Forum forum5 = Forum.builder()
                .name("SciFi Geeks")
                .userId(user5.getId())
                .description("Si te gusta la ciencia ficción y quieres compartir con otros lectores, este foro es para ti. Aquí podrás hablar sobre las obras, novelas y autores del género, desde los clásicos hasta los actuales. También podrás recomendar y encontrar nuevos libros, unirte a clubes de lectura, elegir tus favoritos y más. Este foro es un lugar de intercambio y diversión para los que aman la ciencia ficción y sus posibilidades. ¡Únete al foro y viaja a otros mundos!")
                .img("https://elcomercio.pe/resizer/bVeNT1Ip-SoSlOMX0DQHGUPDZ90=/580x330/smart/filters:format(jpeg):quality(75)/cloudfront-us-east-1.images.arcpublishing.com/elcomercio/DRCLWNKIDZCANB2WUFFEGPJ3V4.jpg")
                .members(users5)
                .membersAmount(users5.size()+1)
                .build();
        Forum forum6 = Forum.builder()
                .name("Los secretos de la fantasía")
                .userId(user1.getId())
                .description("Si te encanta la fantasía y quieres compartir tu pasión con otros lectores, este foro es para ti. Aquí podrás hablar sobre las obras, novelas y autores del género, desde los clásicos como Tolkien, Lewis y Rowling, hasta los contemporáneos como Martin, Rothfuss y Sanderson. También podrás recomendar y descubrir nuevos libros, participar en clubes de lectura, votar por tus favoritos y más. ¡Únete al foro y vive la aventura!")
                .img("https://timothyrjeveland.com/wp-content/uploads/2019/04/the-historic-origins-of-the-fantasy-genre-explained.jpg")
                .members(users1)
                .membersAmount(users1.size() + 1)
                .build();

        Forum forum7 = Forum.builder()
                .name("Los misterios del terror")
                .userId(user2.getId())
                .description("Si te atrae el terror y quieres compartir tu gusto con otros lectores, este foro es para ti. Aquí podrás hablar sobre las obras, novelas y autores del género, desde los clásicos como Poe, Lovecraft y King, hasta los contemporáneos como Hill, Barker y Flynn. También podrás recomendar y encontrar nuevos libros, unirte a clubes de lectura, elegir tus favoritos y más. Este foro es un lugar de intercambio y suspenso para los que aman el terror y sus historias. ¡Únete al foro y siente el miedo!")
                .img("https://upload.wikimedia.org/wikipedia/en/b/ba/NosferatuShadow.jpg")
                .members(users2)
                .membersAmount(users2.size()+1)
                .build();

        Forum forum8 = Forum.builder()
                .name("La novela histórica")
                .userId(user3.getId())
                .description("Si te apasiona la novela histórica y quieres compartir tu interés con otros lectores, este foro es para ti. Aquí podrás hablar sobre las obras, novelas y autores del género, desde los clásicos como Dumas, Pérez-Reverte y Follett, hasta los contemporáneos como Harris, Sepúlveda y Mantel. También podrás recomendar y conocer nuevos libros, participar en clubes de lectura, votar por tus favoritos y más. ¡Únete al foro y viaja al pasado!")
                .img("https://celadonbooks.com/wp-content/uploads/2020/03/Historical-Fiction-scaled.jpg")
                .members(users3)
                .membersAmount(users3.size()+1)
                .build();

        Forum forum9 = Forum.builder()
                .name("Los encantos del romance")
                .userId(user4.getId())
                .description("Si te gusta la novela romántica y quieres compartir tu emoción con otros lectores, este foro es para ti. Aquí podrás hablar sobre las obras, novelas y autores del género, desde los clásicos como Austen, Brontë y García Márquez, hasta los contemporáneos como Sparks, Moyes y Roberts. También podrás recomendar y descubrir nuevos libros, unirte a clubes de lectura, elegir tus favoritos y más. ¡Únete al foro y enamórate!")
                .img("https://escapetoromance.com/wp-content/uploads/sites/172/2017/05/iStock-503130452.jpg")
                .members(users4)
                .membersAmount(users4.size()+1)
                .build();

        Forum forum10 = Forum.builder()
                .name("Los laberintos de Maze Runner")
                .userId(user5.getId())
                .description("Si te gustan las novelas de Maze Runner y quieres compartir tu admiración con otros lectores, este foro es para ti. Aquí podrás hablar sobre las obras, novelas y autor de la serie, James Dashner. También podrás comentar sobre las adaptaciones cinematográficas, los personajes, los misterios y las teorías que rodean al mundo de Maze Runner. ¡Únete al foro y escapa del laberinto!")
                .img("https://prod-ripcut-delivery.disney-plus.net/v1/variant/disney/A3E6EA3E567FCF493467B4EADE7F33FD08CF4228318492B6B79BA90714704BBB/scale?width=1200&aspectRatio=1.78&format=jpeg")
                .members(users5)
                .membersAmount(users5.size()+1)
                .build();

        Tag tag1 = Tag.builder().name("Action").build();
        Tag tag2 = Tag.builder().name("Fiction").build();
        Tag tag3 = Tag.builder().name("Fantasy").build();
        Tag tag4 = Tag.builder().name("NonFiction").build();
        Tag tag5 = Tag.builder().name("Horror").build();
        Tag tag6 = Tag.builder().name("Romance").build();
        Tag tag7 = Tag.builder().name("J.K.Rowling").build();
        Tag tag8 = Tag.builder().name("GeorgeOrwell").build();

        forum1.setTags(List.of(tag1, tag7));
        forum2.setTags(List.of(tag1,tag2));
        forum3.setTags(List.of(tag1,tag2,tag3, tag8));
        forum4.setTags(List.of(tag4));
        forum5.setTags(List.of(tag4));
        forum6.setTags(List.of(tag2,tag3));
        forum7.setTags(List.of(tag2,tag5));
        forum8.setTags(List.of(tag2,tag4));
        forum9.setTags(List.of(tag2,tag6));
        forum10.setTags(List.of(tag1,tag2));

        tagRepository.save(tag1);
        tagRepository.save(tag2);
        tagRepository.save(tag3);
        tagRepository.save(tag4);
        tagRepository.save(tag5);
        tagRepository.save(tag6);
        tagRepository.save(tag7);
        tagRepository.save(tag8);

        forumRepository.save(forum1);
        forumRepository.save(forum2);
        forumRepository.save(forum3);
        forumRepository.save(forum4);
        forumRepository.save(forum5);
        forumRepository.save(forum6);
        forumRepository.save(forum7);
        forumRepository.save(forum8);
        forumRepository.save(forum9);
        forumRepository.save(forum10);
    }
}
