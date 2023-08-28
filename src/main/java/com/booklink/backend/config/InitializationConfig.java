package com.booklink.backend.config;


import com.booklink.backend.model.User;
import com.booklink.backend.repository.UserRepository;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("InitializationConfig")
@Transactional
@Generated
public class InitializationConfig implements CommandLineRunner {
    @Autowired
    UserRepository userRepository;
    final Logger logger = LoggerFactory.getLogger(InitializationConfig.class);



    @Override
    public void run(String... args) throws Exception {
        logger.info("Initialization begin");
        this.loadUsers();
        logger.info("DB data intialized");
    }

    private void loadUsers(){
        User usuario1 = User.builder().username("Facundo").email("facu@gmail.com").password("1234").build();
        User usuario2 = User.builder().username("Tomas").email("tomas@gmail.com").password("to140799").build();
        User usuario3 = User.builder().username("juancho1").email("juan@hotmail.com").password("puc3tmm").build();
        User usuario4 = User.builder().username("FerLopez").email("Lopezf@gmail.com").password("50tru1b").build();
        User usuario5 = User.builder().username("Diego105").email("Diego@gmail.com").password("goo3df1z").build();
        User usuario6 = User.builder().username("miguel flores").email("floresmigue@hotmail.com").password("mifuelrbx1").build();
        User usuario8 = User.builder().username("Vir Tapia").email("viru@gmail.com").password("vel14for").build();
        User usuario9 = User.builder().username("Rubenbarra").email("rubenBarra@gmail.com").password("ru123").build();
        User usuario10 = User.builder().username("darioPereira").email("pereira.dario@gmail.com").password("lhi1769").build();
        userRepository.save(usuario1);
        userRepository.save(usuario2);
        userRepository.save(usuario3);
        userRepository.save(usuario4);
        userRepository.save(usuario5);
        userRepository.save(usuario6);
        userRepository.save(usuario8);
        userRepository.save(usuario9);
        userRepository.save(usuario10);
        }




}
