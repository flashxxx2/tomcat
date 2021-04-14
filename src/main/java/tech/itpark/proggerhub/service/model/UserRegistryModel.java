package tech.itpark.proggerhub.service.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserRegistryModel {
        private String login;
        private String password;
        private String secretWord;
}
