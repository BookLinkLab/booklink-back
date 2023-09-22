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
        //this.loadUsers();
        logger.info("Forums initialization begin");
        //this.loadForums();
        logger.info("DB data intialized");
    }

    private void loadUsers(){
        CreateUserDto user1 = CreateUserDto.builder().username("joaquin").email("joaquinTros@gmail.com").password("tros177jo9").build();
        CreateUserDto usuario2 = CreateUserDto.builder().username("Tomas").email("tomas@gmail.com").password("to140799").build();
        CreateUserDto usuario3 = CreateUserDto.builder().username("juancho1").email("juan@hotmail.com").password("puc3tmm").build();
        CreateUserDto usuario4 = CreateUserDto.builder().username("FerLopez").email("Lopezf@gmail.com").password("50tru1b").build();
        CreateUserDto usuario5 = CreateUserDto.builder().username("Diego105").email("Diego@gmail.com").password("goo3df1z").build();
        CreateUserDto usuario6 = CreateUserDto.builder().username("miguel flores").email("floresmigue@hotmail.com").password("mifuelrbx1").build();
        CreateUserDto usuario8 = CreateUserDto.builder().username("Vir Tapia").email("viru@gmail.com").password("vel14for").build();
        CreateUserDto usuario9 = CreateUserDto.builder().username("Rubenbarra").email("rubenBarra@gmail.com").password("ru123").build();
        CreateUserDto usuario10 = CreateUserDto.builder().username("darioPereira").email("pereira.dario@gmail.com").password("lhi1769").build();
        userService.registerUser(user1);
        userService.registerUser(usuario2);
        userService.registerUser(usuario3);
        userService.registerUser(usuario4);
        userService.registerUser(usuario5);
        userService.registerUser(usuario6);
        userService.registerUser(usuario8);
        userService.registerUser(usuario9);
        userService.registerUser(usuario10);
        }


        private void loadForums(){

            User user1 = userRepository.findByUsername("Tomas").get();
            User user2 = userRepository.findByUsername("Vir Tapia").get();
            User user3 = userRepository.findByUsername("juancho1").get();
            User user4 = userRepository.findByUsername("Rubenbarra").get();
            User user5 = userRepository.findByUsername("darioPereira").get();

            ArrayList<User> usuarios1 = new ArrayList<>();
            usuarios1.add(user1);
            usuarios1.add(user2);
            usuarios1.add(user4);

            ArrayList<User> usuarios5 = new ArrayList<>();
            usuarios5.add(user5);
            usuarios5.add(user3);
            usuarios5.add(user4);



            Forum forum1 = Forum.builder().name("Harry Potter").userId(user1.getId()).description("foro acerca de la mejor saga de libros jamas escrita: Harry Potter").img("https://hips.hearstapps.com/hmg-prod/images/harry-potter-y-la-piedra-filosofal-677923633-large-1636701473.jpg?crop=0.793xw:1.00xh;0.104xw,0&resize=1200:*").members(usuarios1).build();
            Forum forum2 = Forum.builder().name("Narnia").userId(user2.getId()).description("foro sobre Narnia, tocamos todo tipo de temas acerca de este libro, desde debate acerca de los reyes, teorias conspirativas y mas").img("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTvuyDWndxAMMOkZyiptZX7hp-NnPDFWjcUBMGsIj-_&s").members(usuarios1).build();
            Forum forum3 = Forum.builder().name("1984").userId(user3.getId()).description("Nos remitimos a un clásico no solo para habar de él libro en si, sino tambien para reflexionar acerca del mundo en la actualidad").img("https://s1.eestatic.com/2022/03/30/porfolio/revision/661194017_223109283_1706x960.jpg").members(usuarios5).build();
            Forum forum4 = Forum.builder().name("simbolismos de Ana Frank").userId(user4.getId()).description("Nos adentramos un poco más en una de las famosas y erribles historias de nuestra humanidad y debatimos sobre ella").img("https://static.nationalgeographic.es/files/styles/image_3200/public/br0rfp.jpg?w=1600&h=1871").members(usuarios1).build();
            Forum forum5 = Forum.builder().name("los mejores libros de ciencia ficcion").userId(user5.getId()).description("En este foro somos apasionados de la ciencia ficción.Aca hablamos sobre sus mejores obras, novelas y autores.Todos son bienvenidos a opinar ").img("https://es.web.img3.acsta.net/medias/nmedia/18/72/16/76/20065616.jpg").members(usuarios5).build();

            Tag tag1 = Tag.builder().name("Action").build();
            Tag tag2 = Tag.builder().name("Fiction").build();
            Tag tag3 = Tag.builder().name("Fantasy").build();

            forum1.setTags(List.of(tag1));
            forum2.setTags(List.of(tag1,tag2));
            forum3.setTags(List.of(tag1,tag2,tag3));

            tagRepository.save(tag1);
            tagRepository.save(tag2);
            tagRepository.save(tag3);

            forumRepository.save(forum1);
            forumRepository.save(forum2);
            forumRepository.save(forum3);
            forumRepository.save(forum4);
            forumRepository.save(forum5);
    }
}
