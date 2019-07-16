const express=require('express');
const socket=require('socket.io');
const http=require('http');
const fs=require('fs');
const crypto=require('crypto');
//const mysql=require('mysql');
const mongoose=require('mongoose');
const app=express();
const server=http.createServer(app);
const io=socket(server);
/*
var connection=mysql.createConnection({
    host : 'localhost',
    user : 'root',
    password : '비밀번호',
    datebase : 'myfriends'
});

connection.connect(function(err){
    if(err){
        console.log('connect error');
    }else{
        console.log('mysql connected!!');
    }
});
*/
var db=mongoose.connection;
//mongoose.connect('url경로',{useNewUrlParser:true});
mongoose.connect('mongodb://localhost:27017/test',{useNewUrlParser:true});
var socket_ids=[];
app.use('/css',express.static('./static/css'));
app.use('/js',express.static('./static/js'));
app.get('/',function(request,response){
    console.log('/로 접속');
   
    fs.readFile('./static/index.html',function(err,data){
        if(err){
            response.send('error');
        }else{
            response.writeHead(200,{'content-type':'text/html'});
            response.write(data);
            response.end();
        }
    });
    
});
//node js 통신 부분
io.sockets.on('connection',function(socket){
    console.log(socket.id+': user connected');
    
    //클라이언트 연결완료
    socket.on('connectComplete',function(name){
        console.log(name);
        
    });

    //회원가입
    socket.on('joinRequest',function(data){
        console.log('회원가입성공한 유저 db에 저장');
        console.log(data);
        crypto.randomBytes(64,function(err,buf){
            crypto.pbkdf2(data.password,buf.toString('base64'),13737,64,'sha512',
            function(err,key){
                
                db.collection('User').insertOne({
                    email:data.email,
                    password:key.toString('base64'),
                    nicName:data.nicName,
                    chatRoomList:[],
                    friendsList:[]
                });
            });
        });
        
    });
    //웹테스트 용 임시 //////////////
    socket.on('join',function(data){
        socket.join(data.chatRoomID);
    });
    ////////////////////////////////



    //로그인 
    socket.on('loginRequest',function(data){
        console.log('로그인 요청');
        console.log(data);
        socket_ids[data]=socket.id;
        //자신의 닉네임도 같이 넘겨줘야함
        var query={"email":data};
        var projection={"_id":false,"nicName":true}
        db.collection('User').find(query,projection).toArray(function(err,doc){
            if(err)console.log(err);
            else{
                console.log(doc[0].nicName);
                var msg={
                    nicName:doc[0].nicName
                }
                io.to(socket_ids[data]).emit("loginComplete",msg);
            }
        });

        ///////
        
    });

    //친구추가 request
    socket.on('friendsPlusRequest',function(name){
        console.log('친구추가 요청');
        
    });
    socket.on('friendsListRequest',function(data){
        console.log('친구목록 요청');
        var query={"email":data,"friendsList":{$exists:true}};
        var projection={
            "_id":false,
            "friendsList":true
        }
        
        db.collection('User').find(query,projection).toArray(function(err,doc){
            if(err)console.log(err);
            else{
                console.log(doc[0].friendsList);
                io.to(socket_ids[data]).emit('friendsListResponse',doc[0].friendsList);
            }
        });
        
      
    });
    
    //채팅방 관련 request
    socket.on('chatRoomRequest',function(data){
        switch(data.type){
            case 'chatRoomCreate':
                console.log('채팅방생성 요청');
                console.log(data);
                db.collection('User').updateOne({"email":data.userEmail},
                {"$push":{"chatRoomList":{"chatRoomID":data.groupKey,"roomUsers":data.nicNames,"messages":[]}}});
                io.to(socket_ids[data.userEmail]).emit('chatRoomCreated');
                var msg={
                    type: "created",
                    groupKey:data.groupKey,
                    roomUsers:data.nicNames
                }
                io.to(socket_ids[data.userEmail]).emit('chatRoomListUpdate',msg);
            break;
            case 'userLeave':
                console.log('채팅방에서 유저가 나감 요청');

            break;
            case 'chatRoomDestory':
                console.log('채팅방 폭파 요청');

            break;
            case 'chatRoomList':
                console.log('채팅방 리스트 요청');
                console.log(data);
                var query={"email":data.userEmail,"chatRoomList":{$exists:true}};
                var projection={
                    "_id":false,
                    "chatRoomList":true
                }
                db.collection('User').find(query,projection).toArray(function(err,doc){
                    if(err)console.log(err);
                    else{
                        console.log(doc[0].chatRoomList);
                        console.log(doc[0].chatRoomList[0].chatRoomID);
                        console.log(doc[0].chatRoomList.length);
                        for(i=0;i<doc[0].chatRoomList.length;i++){
                            socket.join(doc[0].chatRoomList[i].chatRoomID);
                        }
                        io.to(socket_ids[data.userEmail]).emit('chatRoomListResponse',doc[0].chatRoomList);
                    }
                });
            break;
        }
        
    });

    //데이터 송수신
    socket.on('message',function(data){
        console.log(data);
        var message=({
            ChatRoomID:data.ChatRoomID,
            userID:data.nicName,
            content:data.content,
            date:data.date
        });
       socket.broadcast.to(data.chatRoomID).emit('message',data);
        /*
        db.collection('collection').insertOne({
            ChatRoomID:data.chatRoomID,
            userID:data.userID,
            content:data.content,
            date:data.date
        });
        */
        //socket.broadcast.emit('message',data);
    });

    //어플 종료
    socket.on('disconnect',function(){
        console.log(socket.id+'어플 종료함');
        
    });
});
db.on('error',function(){
    console.log("connect error!");
});
db.once('open',function(){
    console.log('MongoDB connected!!');
    /*
    db.collection('collection').find().toArray(function(err,doc){
        var arr=doc[0].friendsList;
        //json 길이는 이렇게
        console.log(Object.keys(arr).length);
        //friends 리스트
        console.log(arr);
    });
    */
});
server.listen(8006,50000,function(){
    console.log('Web server running...');
});