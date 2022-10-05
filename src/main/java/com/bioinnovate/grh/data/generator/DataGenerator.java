package com.bioinnovate.grh.data.generator;

import com.vaadin.flow.spring.annotation.SpringComponent;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;

@SpringComponent
public class DataGenerator {

    @Bean
    public CommandLineRunner loadData(/*ProduitRepository produitRepository*/) {
        return args -> {
            /*Logger logger = LoggerFactory.getLogger(getClass());
            if (produitRepository.count() != 0L) {
                logger.info("Using existing database");
                return;
            }
            int seed = 123;

            logger.info("Generating demo data");





            logger.info("... generating 100 Produit entities...");
            ExampleDataGenerator<Product> produitRepositoryGenerator = new ExampleDataGenerator<>(Product.class,
                    LocalDateTime.of(2021, 2, 3, 0, 0, 0));
            produitRepositoryGenerator.setData(Product::setId, DataType.ID);
            produitRepositoryGenerator.setData(Product::setName, DataType.FIRST_NAME);
            produitRepositoryGenerator.setData(Product::setCodePharmacieCentrale, DataType.LAST_NAME);
            produitRepositoryGenerator.setData(Product::setCodeBarre, DataType.EMAIL);
            produitRepositoryGenerator.setData(Product::setNombre, DataType.PHONE_NUMBER);
            produitRepositoryGenerator.setData(Product::setRefrenceInterne, DataType.OCCUPATION);
            produitRepositoryGenerator.setData(Product::setCategorieReseau, DataType.BOOLEAN_10_90);
            produitRepository.saveAll(produitRepositoryGenerator.create(100, seed));

            logger.info("Generated demo data");

             */
        };
    }

}