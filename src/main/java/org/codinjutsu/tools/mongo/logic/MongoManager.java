/*
 * Copyright (c) 2012 David Boissier
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.codinjutsu.tools.mongo.logic;

import com.mongodb.DB;
import com.mongodb.Mongo;
import org.apache.commons.lang.StringUtils;
import org.codinjutsu.tools.mongo.MongoConfiguration;
import org.codinjutsu.tools.mongo.model.MongoDatabase;
import org.codinjutsu.tools.mongo.model.MongoServer;

import java.net.UnknownHostException;
import java.util.List;

public class MongoManager {

    public void connect(String serverName, int serverPort, String username, String password, String dbname) {
        try {
            Mongo mongo = new Mongo(serverName, serverPort);
            DB test = mongo.getDB(dbname);
            if (StringUtils.isNotBlank(username)) {
                test.authenticate(username, password.toCharArray());
            }
        } catch (UnknownHostException ex) {
            throw new ConfigurationException(ex);
        }
    }

    public MongoServer loadDatabaseCollections(MongoConfiguration configuration) {
        try {
            Mongo mongo = new Mongo(configuration.getServerName(), configuration.getServerPort());

            MongoServer mongoServer = new MongoServer(configuration.getServerName(), configuration.getServerPort());
            if (StringUtils.isNotBlank(configuration.getDefaultDatabase())) {
                DB database = mongo.getDB(configuration.getDefaultDatabase());
                MongoDatabase mongoDatabase = new MongoDatabase(database.getName());
                mongoDatabase.addCollections(database.getCollectionNames());
                mongoServer.addDatabase(mongoDatabase);
                return mongoServer;
            }

            List<String> databaseNames = mongo.getDatabaseNames();
            for (String databaseName : databaseNames) {
                DB database = mongo.getDB(databaseName);
                MongoDatabase mongoDatabase = new MongoDatabase(database.getName());
                mongoDatabase.addCollections(database.getCollectionNames());
                mongoServer.addDatabase(mongoDatabase);
            }
            return mongoServer;
        } catch (UnknownHostException ex) {
            throw new ConfigurationException(ex);
        }
    }
}