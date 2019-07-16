package com.example.myfriends;

import java.nio.file.attribute.FileAttribute;

import io.realm.DynamicRealm;
import io.realm.FieldAttribute;
import io.realm.Realm;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;

public class Migration implements RealmMigration {

    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
        RealmSchema schema=realm.getSchema();
        /*
        @PrimaryKey
    private long chatID;

    private String chatRoomID;//외래키
    private String name;
    private String content;
    private String date;
    private int type;

         */
        if(oldVersion==0){
            schema.create("ChatRealmVO")
                    .addField("chatID",long.class, FieldAttribute.PRIMARY_KEY)
                    .addField("chatRoomID",String.class)
                    .addField("name",String.class)
                    .addField("content",String.class)
                    .addField("date",String.class)
                    .addField("type",int.class);
            oldVersion++;
        }
    }
}
