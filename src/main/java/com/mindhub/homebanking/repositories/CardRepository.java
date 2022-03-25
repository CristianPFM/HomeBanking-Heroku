package com.mindhub.homebanking.repositories;

import com.mindhub.homebanking.models.Card;
import com.mindhub.homebanking.models.CardType;
import com.mindhub.homebanking.models.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface CardRepository extends JpaRepository<Card, Long> {
    //metodo para contar las tarjetas segun tipo pasandole el cliente correspondiente
    public int countByClientAndCardType(Client client, CardType cardType);
    public void removeCardById(Long id);
}