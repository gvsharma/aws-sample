package com.example.awssample;

import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.mapper.StaticTableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.PutItemEnhancedRequest;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.ProvisionedThroughput;

import static software.amazon.awssdk.enhanced.dynamodb.mapper.StaticAttributeTags.primaryPartitionKey;

@Component("DynamoDBEnhanced")
public class DynamoDBEnhanced {
    private final ProvisionedThroughput DEFAULT_PROVISIONED_THROUGHPUT =
            ProvisionedThroughput.builder()
                    .readCapacityUnits(50L)
                    .writeCapacityUnits(50L)
                    .build();
    private final TableSchema<GreetingItems> TABLE_SCHEMA =
            StaticTableSchema.builder(GreetingItems.class)
                    .newItemSupplier(GreetingItems::new)
                    .addAttribute(String.class, a -> a.name("idblog")
                            .getter(GreetingItems::getId)
                            .setter(GreetingItems::setId)
                            .tags(primaryPartitionKey()))
                    .addAttribute(String.class, a -> a.name("author")
                            .getter(GreetingItems::getName)
                            .setter(GreetingItems::setName))
                    .addAttribute(String.class, a -> a.name("title")
                            .getter(GreetingItems::getTitle)
                            .setter(GreetingItems::setTitle))
                    .addAttribute(String.class, a -> a.name("body")
                            .getter(GreetingItems::getMessage)
                            .setter(GreetingItems::setMessage))
                    .build();

    public void injectDynamoItem(Greeting item){
        Region region = Region.US_EAST_1;
        DynamoDbClient ddb = DynamoDbClient.builder()
                .region(region)
                .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                .build();
        try {

            DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
                    .dynamoDbClient(ddb)
                    .build();

            // Create a DynamoDbTable object
            DynamoDbTable<GreetingItems> mappedTable = enhancedClient.table("Greeting", TABLE_SCHEMA);
            GreetingItems gi = new GreetingItems();
            gi.setName(item.getName());
            gi.setMessage(item.getBody());
            gi.setTitle(item.getTitle());
            gi.setId(item.getId());

            PutItemEnhancedRequest enReq = PutItemEnhancedRequest.builder(GreetingItems.class)
                    .item(gi)
                    .build();

            mappedTable.putItem(enReq);

        } catch (Exception e) {
            e.getStackTrace();
        }
    }

}
