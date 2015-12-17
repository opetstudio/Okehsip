# Okehsip

Alamat server
http://okehsip-rtpoai.rhcloud.com/

Daftar API:

Host: okehsip-rtpoai.rhcloud.com

POST androidUserLoginURL
	Req: email,pass,client
	Resp: {err_code:"",err_msg:"",notif:"",authtoken:""}
	
POST androidUserSignupURL
	Req: name,email,pass,client,vercode
	Resp: {err_code:"",err_msg:"",notif:""}
	
POST androidAccountVerifyURL
	Req: email,vercode
	Resp: {err_code:"",err_msg:"",notif:""}
	
POST androidForgotPasswordURL
	Req: email,vercode
	Resp: {err_code:"",err_msg:"",notif:""}
	
POST androidForgotPasswordURL
	Req: email,vercode,pass,client
	Resp: {err_code:"",err_msg:"",notif:""}
	

Verification Code:
http://okehsip-rtpoai.rhcloud.com/getVercode?email=[nama email]

Remove Account:
http://okehsip-rtpoai.rhcloud.com/RmAccountByEmail?email=[nama email]

