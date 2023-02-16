package com.budgetapi;

import com.budgetapi.account.model.Account;
import com.budgetapi.account.repository.AccountRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.HashSet;

@SpringBootApplication
public class BudgetapiApplication {

    public static void main(String[] args) {
        SpringApplication.run(BudgetapiApplication.class, args);
    }

     @Bean
     CommandLineRunner initDatabase(AccountRepository repository) {
     return args -> extracted(repository);
     }

     private void extracted(AccountRepository repository) {
       var accounts = new HashSet<Account>();
       var accounts2 = new HashSet<Account>();
         var accounts3 = new HashSet<Account>();
         var accounts4 = new HashSet<Account>();
         var accounts5 = new HashSet<Account>();
         var accounts6 = new HashSet<Account>();
         var accounts7 = new HashSet<Account>();
         var accounts8 = new HashSet<Account>();

         System.out.println(accounts2);

       for (int i = 1; i < 20; i++) {
           accounts.add(Account.builder()
                .name("Nubank" + i)
                .currency("BRL")
                .build());
       }

       repository.saveAll(accounts);
     }

}
