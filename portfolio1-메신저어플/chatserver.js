const express=require('express');
const socket=require('socket.io');
const http=require('http');
const multiparty=require('multiparty');
/*
const multipartMiddleware = multiparty({
    maxFilesSize : 1024*1024*10
});
*/
const exepath=require('path')
const fs=require('fs');
const mkdirp=require('mkdirp');
const crypto=require('crypto');
const mysql=require('mysql');
const mongoose=require('mongoose');
const qs=require('querystring');
const app=express();
const server=http.createServer(app);
const io=socket(server);

var connection=mysql.createConnection({
    host : 'jjjj1352.cafe24.com',
    user : 'jjjj1352',
    password : 'qkrwlgy37@',
    datebase : 'jjjj1352',
    charset: 'utf8_bin'
});

connection.connect(function(err){
    if(err){
        console.log('connect error');
    }else{
        console.log('mysql connected!!');
    }
});


//var db=mongoose.connection;
//mongoose.connect('url경로',{useNewUrlParser:true});
//mongoose.connect('mongodb://localhost:27017/test',{useNewUrlParser:true});
var socket_ids=[];
var sockets=[];



app.use('/css',express.static('./static/css'));
app.use('/js',express.static('./static/js'));
app.use('/files',express.static('./files'));
app.use('/uploadFile',express.static('./uploadFile'));
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
app.post('/files/profile',function(request,response){
    console.log('사진 받기')
    if(request.method=='POST'){
        console.log('post 도착');
        var body='';
        request.on('data',function(data){
            body+=data;
        });
        request.on('end',function(){
            var post=qs.parse(body);
            var buf=Buffer.from(post.image,'base64');
            fs.writeFile('./files/profile/'+post.name+'.png',buf,function(err){
                if(err)console.log(err);
                else{
                    console.log('end');
                    response.end();
                }
                
            });
        });
    }
});
app.post('/files/profileBackground',function(request,response){
    console.log('사진 받기')
    if(request.method=='POST'){
        console.log('post 도착');
        var body='';
        request.on('data',function(data){
            body+=data;
        });
        request.on('end',function(){
            var post=qs.parse(body);
            var buf=Buffer.from(post.image,'base64');
            fs.writeFile('./files/profileBackground/'+post.name+'.png',buf,function(err){
                if(err)console.log(err);
                else{
                    console.log('end');
                    response.end();
                }
            });
        });
    }
});
/*
app.post('/uploadFile',multipartMiddleware,function(req,res){
    console.log(req.body,req.files)
    var img=req.files['data']
    mkdirp('.'+req.body.savePath,function(err){
        if(err)console.log('already exist dir'); 
     });
     
    fs.writeFile('.'+req.body.savePath+'/'+req.body.fileName,img,function(err){
        if(err)console.log(err);
    });
    console.log(img.size);
});
*/
app.post('/',function(request,response){
    var form=new multiparty.Form();
    var url;
    form.parse(request);
    form.on('field',function(name,value){
        console.log('filed 들어옴');
        if(name=='downloadRequest'){
            url=value;
            var readStream=fs.createReadStream('.'+url);
            readStream.pipe(response);
        }
        if(name=='fileExtensionRequest'){
            var fileStatus=fs.statSync('.'+value);
            var fileName=value.substring(value.lastIndexOf('/')+1);
            console.log(fileStatus.size+'  '+fileName);
            response.send({fileExtension:exepath.extname(value),
            fileName:fileName,
            fileSize:fileStatus.size});
        }
    });
});
app.post('/uploadFile',function(request,response){
    console.log('사진 받기')
    var savePath;
    var form=new multiparty.Form({
        maxFilesSize : 1024*1024*10
     });
    form.parse(request);
    form.on('field',function(name,value){
        console.log('field 들어옴');
        if(name=='savePath'){
            savePath=value;
        }
    });
    form.on('part',function(part){
        console.log('part 들어옴');
        var filename;
        var size;
        var writeStream;
        if(part.filename){
            filename=part.filename;
            size=part.byteCount;
        }else{
            part.resume();
        }
        if(savePath){
            console.log(savePath);
            mkdirp('.'+savePath,function(err){
                if(err)console.log('already exist dir'); 
                writeStream = fs.createWriteStream('.'+savePath+'/'+filename);
                writeStream.filename = filename;
                part.pipe(writeStream);
                console.log(writeStream);
             });
             
        }else{
            part.resume();
        }
        
        part.on('end',function(){
            console.log(filename+' Part read complete');
            writeStream.end();
        });
        
    });
    form.on('close',function(){
        response.status(200).send('Upload complete');
    });
    
});

//node js 통신 부분
io.sockets.on('connection',function(socket){
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
                //mysql version
                var query='INSERT INTO jjjj1352.user (email,password,nicname) VALUES(?,?,?)';
                var params=[data.email,key.toString('base64'),data.nicName];
                connection.query(query,params,function(err,rows,fields){
                    if(err)console.log(err);
                    console.log(rows);
                });
                
                //mongodb version
                /*
                db.collection('User').insertOne({
                    email:data.email,
                    password:key.toString('base64'),
                    nicName:data.nicName,
                    chatRoomList:[],
                    friendsList:[]
                });
                */
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
        console.log('로그인 요청 : ' +socket.id);
        console.log(data);
        socket_ids[data]=socket.id;
        //코드 테스트중//////////////////////////
        sockets[data]=socket;//room join을위해
        ///////////////////////////////////////
        //자신의 닉네임도 같이 넘겨줘야함
        //mysql version
        var query='SELECT email,nicname FROM jjjj1352.user WHERE email=?';
        var params=[data];
        connection.query(query,params,function(err,rows,fields){
            if(err)console.log(err);
            else{
                if(rows.length==0){
                    console.log('존재하지 않는 유저입니다');
                    var msg={
                        type:'joinRequest'
                    }
                    io.to(socket_ids[data]).emit("loginComplete",msg);
                }else{
                    var msg={
                        type:'joined',
                        nicName:rows[0].nicname.toString()
                    }
                    console.log('mysql test : '+msg.nicName);
                    io.to(socket_ids[data]).emit("loginComplete",msg);
                }
            }
        });




        //mongodb version
        /*
        var query={"email":data};
        var projection={"_id":false,"nicName":true}
        db.collection('User').find(query,projection).toArray(function(err,doc){
            if(err){
                console.log(error);
            }
            else{
                if(Object.keys(doc).length==0){
                    console.log('존재하지 않는 유저입니다');
                    var msg={
                        type:'joinRequest'
                    }
                    io.to(socket_ids[data]).emit("loginComplete",msg);
                }else{
                    var msg={
                        type:'joined',
                        nicName:doc[0].nicName
                    }
                    io.to(socket_ids[data]).emit("loginComplete",msg);
                }
                
            }
        });
        */
        ///////
        
    });
    socket.on('reconnect',function(email){
        socket_ids[email]=socket.id;
        sockets[email]=socket;
        //채팅방 재연결
        var query={"email":email,"chatRoomList":{$exists:true}};
                var projection={
                    "_id":false,
                    "chatRoomList":true
                }
                db.collection('User').find(query,projection).toArray(function(err,doc){
                    if(err)console.log(err);
                    else{
                        for(i=0;i<doc[0].chatRoomList.length;i++){
                            socket.join(doc[0].chatRoomList[i].chatRoomID);
                        }
                    }
                });
    });
    //친구삭제 
    socket.on('friendsDelete',function(data){
        console.log('친구삭제 요청');
        var query='DELETE FROM jjjj1352.friendslist WHERE email=? and nicname=?';
        var params=[data.userEmail,data.nicName];
        connection.query(query,params,function(err,rows,fields){
            console.log('친구삭제');
        });
    });
    //친구추가 request
    socket.on('friendsPlusRequest',function(data){
        console.log('친구추가 요청');
        //mysql version
        var query='SELECT nicname FROM jjjj1352.user WHERE nicname=?';
        var params=[data.nicName];
        var result=false;
        connection.query(query,params,function(err,rows,fields){
            if(rows.length==0){
                result=false;
            }else{
                if(rows[0].nicname==data.myNicName){
                    result=false;
                }else{
                    result=true;
                    var query='INSERT INTO jjjj1352.friendslist (email,nicname) VALUES(?,?)';
                    var params=[data.userEmail,data.nicName];
                    connection.query(query,params,function(err,rows,fields){
                        console.log('친구목록추가');
                    });
                }
            }
            var msg={
                result:result
            }
            io.to(socket_ids[data.userEmail]).emit('friendsPlusResponse',msg);
        });


        //mongodb version
        /*
        db.collection('User').find().toArray(function(err,doc){
            var result=false;
            var len=Object.keys(doc).length;
            for(i=0;i<len;i++){
                if(data.nicName==doc[i].nicName){
                    result=true;
                    db.collection('User').updateOne({"email":data.userEmail},
                {"$push":
                {"friendsList":
                {"nicName":data.nicName,"stateMessage":""}}});
                    break;
                }
            }
            var msg={
                result:result
            }
            if(result){
                io.to(socket_ids[data.userEmail]).emit('friendsPlusResponse',msg);
            }else{
                io.to(socket_ids[data.userEmail]).emit('friendsPlusResponse',msg);
            }
            
        });
        */
    });
    socket.on('friendsListRequest',function(data){
        console.log('친구목록 요청');
        //mysql version
        var query='SELECT * FROM jjjj1352.friendslist WHERE email=?';
        var params=[data];
        connection.query(query,params,function(err,rows,fields){
            if(err)console.log(err);
            else{
                if(rows.length==0){
                    console.log('데이터베이스 오류');
                    var friendinfo=[];
                    io.to(socket_ids[data]).emit('friendsListResponse',friendinfo);
                }else{
                    console.log(rows);
                    var friendinfo=[];
                    /*
                    var info={
                        nicName:"",
                        profileImage:"",
                        backgroundImage:"",
                        stateMessage:""
                    }
                   */
                    for(i=0;i<rows.length;i++){
                        var info={};
                        info.nicName=rows[i].nicname.toString();
                        info.profileImage=rows[i].profileimage;
                        info.backgroundImage=rows[i].backgroundimage;
                        if(rows[i].statemessage!=null){
                            info.stateMessage=rows[i].statemessage.toString();
                        }else{
                            info.stateMessage="";
                        }
                        
                        friendinfo.push(info);
                        console.log(rows[i].nicname.toString());
                    }
                    console.log(rows.length);
                    io.to(socket_ids[data]).emit('friendsListResponse',friendinfo);
                }
            }
        });

        //mongodb version
        /*
        var query={"email":data,"friendsList":{$exists:true}};
        var projection={
            "_id":false,
            "friendsList":true
        }
        db.collection('User').find(query,projection).toArray(function(err,doc){
            if(err)console.log(err);
            else{
                if(Object.keys(doc).length==0){
                    console.log('데이터베이스 오류');
                }else{
                    console.log(doc[0].friendsList);
                    io.to(socket_ids[data]).emit('friendsListResponse',doc[0].friendsList);
                }
                
            }
        });
        */
      
    });
    
    //채팅방 관련 request
    socket.on('chatRoomRequest',function(data){
        switch(data.type){
            case 'chatRoomCreate':
                console.log('채팅방생성 요청');
                var names=data.nicNames;
                var msg={
                    type: "created",
                    groupKey:data.groupKey,
                    roomUsers:data.nicNames
                }
                var nicNames=data.nicNames.replace("[","")
                .replace(/ /gi,"")
                .replace("]","")
                .split(",");
                console.log(data.nicNames);
                
                for(var i in nicNames){
                    console.log(nicNames[i]);
                    var query='INSERT INTO jjjj1352.chatroom (chatroomid,nicname) VALUES(?,?)'
                    var params=[data.groupKey,nicNames[i]];
                    console.log(nicNames[i]+'추가');
                    connection.query(query,params,function(err,rows,fields){
                    });
                    var query='SELECT email FROM jjjj1352.user WHERE nicname=?';
                    var params=[nicNames[i]];
                    connection.query(query,params,function(err,rows,fields){
                        io.to(socket_ids[rows[0].email]).emit('chatRoomListUpdate',msg);
                    });
                    
                    /*
                    db.collection('User').updateOne({"nicName":nicNames[i]},
                {"$push":{"chatRoomList":{"chatRoomID":data.groupKey,"roomUsers":data.nicNames,"messages":[]}}});
                    db.collection('User').find({"nicName":nicNames[i]}).toArray(function(err,doc){
                        if(err)console.log(err);
                        console.log('chatRoomListUpdate'+doc[0].email);
                        if(sockets[doc[0].email]!=null){
                            sockets[doc[0].email].join(data.groupKey);
                        }
                        io.to(socket_ids[doc[0].email]).emit('chatRoomListUpdate',msg);
                    });
                    */
                }
                //자신의 채팅방 업데이트
                io.to(socket_ids[data.userEmail]).emit('chatRoomCreated');
                //io.to(socket_ids[data.userEmail]).emit('chatRoomListUpdate',msg);
            break;
            case 'userLeave':
                console.log('채팅방에서 유저가 나감 요청');
                //사용자의 이메일과 채팅방 키가 필요
                //mysql version
                var query='DELETE FROM jjjj1352.chatroom WHERE chatroomid=? and nicname=?';
                console.log(data.groupKey);
                var params=[data.groupKey,data.nicName];
                connection.query(query,params,function(err,rows,fields){
                    console.log('채팅방삭제');
                });

                //mongodb version
                /*
                db.collection('User').updateOne({"email":data.userEmail},
                {"$pull":{"chatRoomList":{"chatRoomID":data.groupKey}}});
                */
                sockets[data.userEmail].leave(data.groupKey)
            break;
            case 'chatRoomDestory':
                console.log('채팅방 폭파 요청');

            break;
            case 'chatRoomList':
                console.log('채팅방 리스트 요청');
                console.log(data);
                //mysql version
                var query='SELECT chatroomid,nicname FROM jjjj1352.chatroom WHERE chatroomid=any(SELECT chatroomid FROM jjjj1352.chatroom WHERE nicname=?)'
                var params=data.nicName;
                connection.query(query,params,function(err,rows,fields){
                    if(err)console.log(err);
                    else{
                        if(rows.length==0){
                            var chatRoomInfo=[];
                            io.to(socket_ids[data.userEmail]).emit('chatRoomListResponse',chatRoomInfo);
                        }else{
                            var chatRoomInfo=[];
                            for(i=0;i<rows.length;i++){
                                chatRoomInfo.push({
                                    chatRoomID:rows[i].chatroomid.toString(),
                                    name:rows[i].nicname.toString()
                                });
                                socket.join(rows[i].chatroomid.toString());
                                console.log("test : "+rows[i].chatroomid.toString()+"  "+rows[i].nicname.toString());
                            }
                            io.to(socket_ids[data.userEmail]).emit('chatRoomListResponse',chatRoomInfo)
                        }
                    }
                });
                //mongodb version
                /*
                var query={"email":data.userEmail,"chatRoomList":{$exists:true}};
                var projection={
                    "_id":false,
                    "chatRoomList":true
                }
                db.collection('User').find(query,projection).toArray(function(err,doc){
                    if(err)console.log(err);
                    else{
                        //console.log(doc[0].chatRoomList);
                        //console.log(doc[0].chatRoomList[0].chatRoomID);
                        //console.log(doc[0].chatRoomList.length);
                        for(i=0;i<doc[0].chatRoomList.length;i++){
                            socket.join(doc[0].chatRoomList[i].chatRoomID);
                        }
                        io.to(socket_ids[data.userEmail]).emit('chatRoomListResponse',doc[0].chatRoomList);
                    }
                });
                */
            break;
        }
        
    });

    //데이터 송수신
    socket.on('message',function(data){
        console.log(data);
        var message=({
            chatRoomID:data.chatRoomID,
            nicName:data.nicName,
            content:data.content,
            date:data.date
        });
        socket.join(data.chatRoomID);
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
        var arr=Object.values(socket_ids);
        for(i=0;i<arr.length;i++){
            if(arr[i]==socket.id){
                console.log(socket.id+'어플 종료함');
            }
        }
        socket.emit('reconnect');
    });

});
/*
db.on('error',function(){
    console.log("connect error!");
});
db.once('open',function(){
    console.log('MongoDB connected!!');
    
    db.collection('collection').find().toArray(function(err,doc){
        var arr=doc[0].friendsList;
        //json 길이는 이렇게
        console.log(Object.keys(arr).length);
        //friends 리스트
        console.log(arr);
    });
    
});
*/
server.listen(8006,50000,function(){
    console.log('Web server running...');
});