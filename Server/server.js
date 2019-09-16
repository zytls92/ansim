var express = require('express');
var app = express();

var bodyParser = require('body-parser');
var session = require('express-session');
var bcrypt = require('bcrypt-nodejs');
var cookieParser = require('cookie-parser');

// Setting MongoDB
var mongoose = require('mongoose');
mongoose.connect('mongodb://localhost:27017/ansim');

var db = mongoose.connection;

db.on('error', function(){
	console.log('MongoDB Connection Failed!');
});
db.once('open', function(){
	console.log('MongoDB Connected!');
});

//session secret
app.use(express.json());
app.use(bodyParser.urlencoded({extended: false}));
app.use(bodyParser.json());
app.use(session({ secret: 'keyboard cat', resave: true, saveUninitialized: true })); 
app.use(cookieParser());

// //Describe MongoDB Collection
var users = mongoose.Schema({
	//email is conceptual PK
	email : String,
	name : String,
	password : String,
	smsName : [String],
	smsNumber : [String],
	bookmark1Lat : [Number],
	bookmark1Lon : [Number],
	bookmark1Date : Date,
	bookmark2Lat : [Number],
	bookmark2Lon : [Number],
	bookmark2Date : Date,
	bookmark3Lat : [Number],
	bookmark3Lon : [Number],
	bookmark3Date : Date,
	bookmark4Lat : [Number],
	bookmark4Lon : [Number],
	bookmark4Date : Date,
	bookmark5Lat : [Number],
	bookmark5Lon : [Number],
	bookmark5Date : Date,					
});

var accidents = mongoose.Schema({
	//email is Conceptual PK
	email : String,
	name : String,
	locationX : Number,
	locationY : Number,
	date : Date	
});


// // Connect Schema Model to MongoDB Collection
var User = mongoose.model('user', users);
var Accident = mongoose.model('accident', accidents);

// post signup process
app.post('/signup', function(req, res){
//	console.log(req);
	// password cryption
	var generateHash = function(password){
		return bcrypt.hashSync(password, bcrypt.genSaltSync(8), null);
	}

	// find User
	User.findOne({email : req.body.email}, function(error, user){
		if(error){
			console.log(error);
		} else if(user == null){
			// if not yet sign up
			// cryption password
			var userPassword = generateHash(req.body.pw);
			
			// create New User Data
			var newUser = new User({
				email : req.body.email,
				name : req.body.name,
				password : userPassword
			});
			
			// save user data to MongoDB
			newUser.save(function(error, data){
				if(error){
					console.log('Insert Error!');
				} else{
					console.log('Successfully newUser Signup!');
				}
			});
		} else{
			console.log('Already hava same ID');
		}
	});
	res.send('Did you hear me?');
});


// post signin process
app.post('/signin', function(req, res){
//	console.log(req);
	// check password is valid
	var isValidPassword = function(userpass, password){
		return bcrypt.compareSync(password, userpass);
	};

//	console.log(req);
//	retrieve user information
	User.findOne({email : req.body.email}, function(error, user){
		if(error){
			console.log(error);
		} else if(user == null){
			// no data
			console.log('Not our User');
			res.send('LoginFail');
		} else{			
			// if password is valid
			if(isValidPassword(user.password, req.body.pw)){
				res.send(user.email);
			} else {
				// password invalid
				console.log('Wrong Password!');
				res.send('LoginFail');
			}
		}
	});
});


// post retrieve and show friends list 
app.post('/retrieve/friends', function(req, res){
//	console.log(req);
	User.findOne({email : req.body.session}, function(error, user){
		if(error){
			console.log(error);
		} else {
			if(user.smsName[0] === null){
				console.log('nodata');
//				res.send("NODATA");
			} else{
				var arr = [];
				let index = 0;

				while(true){
					if(user.smsName[index] == null) break;
					else{
						arr.push(user.smsName[index]);
						arr.push(user.smsNumber[index]);
					}
					index++;
				}
				console.log(arr);
				res.send(arr);
			}
		}
	});
});


// post insert a new friend information
app.post('/insert/friends', function(req, res){
	console.log(req);
	User.findOneAndUpdate({email : req.body.session}, {$push: {smsName : req.body.name}}, function(err, data){
		if (err){
			console.log(err);
			res.send('error');
		} else {
			User.findOneAndUpdate({email : req.body.session}, {$push: {smsNumber : req.body.number}}, function(err, data){
				if(err){
					console.log(err);
					res.send('qwe');
				} else{
					console.log('success');
					res.send('Add');
				}
			});
		}
	});
});

// post retrieve and show bookmark information
app.post('/retrieve/bookmark', function(req, res){
	console.log(req.session.user_uid);
});

// post select one bookmark
app.post('/select/bookmark', function(req, res){
	console.log(req.session.user_uid);
});

// post check and insert new bookmark 
app.post('insert/bookmark', function(req, res){
	console.log(req.session.user_uid);
});

app.post('insert/accident', function(req, res){
	console.log(req.session.user_uid);
});

// var acci1 = new Accident({
// 	email : 'guysuh123@naver.com',
// 	name : 'Nam',
// 	location : {
// 		latitude : 38.000001,
// 		longitude : 42.195002
// 	},
// 	date : Date.now()
// });

// acci1.save(function(error, data){
// 	if(error){
// 		console.log('insert error!');
// 	} else{
// 		console.log('saved2!');
// 	}
// });

// var user1 = new User({
// 	email : 'guysuh123@naver.com',
// 	name : 'Nam',
// 	password : '@antjs3671',
// 	smsName : ['NAM', 'KIN', 'LEE', 'JO', 'SONG'],
// 	smsNumber :  [
// 		'01011111111',
// 		'01012345678',
// 		'01099999999',
// 		'01022222222',
// 		'01034567890'
// 	]
// });

// user1.save(function(error, data){
// 	if(error){
// 		console.log('insert error!');
// 	} else{
// 		console.log('save!');
// 	}
// });

app.listen(3000, function(){
	console.log("server start!");
	// User.find({name : "User Name"}).exec(function(error, users){
	// 	console.log(users);
	// 	if(users.bookmark5Lat == null) console.log("is OK!");
	// 	else console.log("it is not good way");
	// });	
	// User.findOne({name : "User Name"}).exec(function(error, user){
	// 	console.log(user._id);
	// })
});