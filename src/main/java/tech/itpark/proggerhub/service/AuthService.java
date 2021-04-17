package tech.itpark.proggerhub.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tech.itpark.proggerhub.crypto.PasswordHasher;
import tech.itpark.proggerhub.crypto.TokenGenerator;
import tech.itpark.proggerhub.exception.*;
import tech.itpark.proggerhub.repository.AuthRepository;
import tech.itpark.proggerhub.repository.model.UserTokenModel;
import tech.itpark.proggerhub.security.AuthProvider;
import tech.itpark.proggerhub.security.Authentication;
import tech.itpark.proggerhub.service.model.UserAuthModel;
import tech.itpark.proggerhub.service.model.UserModel;
import tech.itpark.proggerhub.service.model.UserRegistryModel;
import tech.itpark.proggerhub.service.model.UserRestoreModel;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService implements AuthProvider {
  private final AuthRepository repository;
  private final PasswordHasher hasher;
  private final TokenGenerator tokenGenerator;

  public long register(UserRegistryModel model) {
    if (model.getLogin() == null) {
      throw new BadLoginException();
    }
    if (!model.getLogin().matches("^[a-zA-Z0-9]{3,15}$")) {
      throw new BadLoginException();
    }
    return repository.save(new tech.itpark.proggerhub.repository.model.UserModel(model.getLogin().trim().toLowerCase(),
        hasher.hash(model.getPassword()), hasher.hash(model.getSecretWord())
    ));
  }

  public String login(UserModel model) {
    final var user = repository.findByLogin(model.getLogin()).orElseThrow(UserNotFoundException::new);

    if (!hasher.match(user.getHash(), model.getPassword())) {
      throw new PasswordsNotMatchedException();
    }

    final var token = tokenGenerator.generate();
    repository.save(new UserTokenModel(user.getId(), token));
    return token;
  }

  public Authentication authenticate(String token) {

    return repository.findByToken(token)
        .map(o -> (Authentication) new UserAuthModel(o.getId(), o.getRoles()))
        .orElse(Authentication.anonymous())
        ;
  }

  public void removeById(Authentication auth) {
    if (!auth.hasAnyRoles("ROLE_ADMIN")) {
      throw new PermissionDeniedException();
    }
    // ok
  }

  public void restore(UserRestoreModel model) {
    final var user = repository.getRestoreModel(model.getLogin()).orElseThrow(UserNotFoundException::new);
    if (!hasher.match(user.getSecretWord(), model.getSecretWord())) {
      throw new SecretWordNotMatchedException();
    }
  }
}
