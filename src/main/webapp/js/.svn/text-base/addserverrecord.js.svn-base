
$(document).ready(function(){ 
	var areaParentId = 0;
	/**
	 * 获取当前用户所在地区，将户籍地与现居住地默认设置为当前用户地区
	 */
	$.ajax({
		type : "post",
		contentType : "application/x-www-form-urlencoded;charset=UTF-8",
		url : '/threeExaminations/areaController/searchByAreaParentId.do',
		async : false,
		data : {
			areaParentId : areaParentId
		},
		dataType : 'json',
		success : function(msg) {
			if (msg.result == true) {
				var province1 = "";
				$.each(msg.areaList, function(key, val) {
					province1 += "<option>" + val.areaName + "</option>";
				});
				//户籍
				$("#womanProvince").empty().append(province1);

				
				var province2 = $("#womanProvince").val();
				findArea(province2,"#womanCity");
				$("#womanCity").val(msg.city);//设置市
				var city2 = $("#womanCity").val();
				findArea(city2,"#womanCounty");
				$("#womanCounty").val(msg.county);//设置县区
				var county2 = $("#womanCounty").val();
				findArea(county2,"#womanTown");
				$("#womanTown").val(msg.town);//设置镇
				
				//现居住地
				$("#womanCurProvince").empty().append(province1); 
				var province2 = $("#womanCurProvince").val();
				findArea(province2,"#womanCurCity");
				$("#womanCurCity").val(msg.city);//设置市
				var city2 = $("#womanCurCity").val();
				findArea(city2,"#womanCurCounty");
				$("#womanCurCounty").val(msg.county);//设置县区
				var county2 = $("#womanCurCounty").val();
				findArea(county2,"#womanCurTown");
				$("#womanCurTown").val(msg.town);//设置镇
				
				
			}
		
		},
		error : function(msg) {
			alert("网络超时！");
		}
	});
	/**
	 * 填写完所有的妇女服务记录信息，点击保存，更新数据库
	 */
	$("#save").click(function (){
		if(cheack()){
			var womanName=$("#womanName").val();
			var womanIdCard=$("#womanIdCard").val();
			var womanProvince=$("#womanProvince").val();
			var womanCity=$("#womanCity").val();
			var womanCounty=$("#womanCounty").val();
			var womanTown=$("#womanTown").val();
			var womanVallige=$("#womanVallige").val();
			var womanCurProvince=$("#womanCurProvince").val();
			var womanCurCity=$("#womanCurCity").val();
			var womanCurCounty=$("#womanCurCounty").val();
			var womanCurTown=$("#womanCurTown").val();
			var womanCurVallige=$("#womanCurVallige").val();
			var liveState=$("#liveState").val();
			var checkYear=$("#checkYear").val();
			var checkPatch=$("#checkPatch").val();
			var checkDate=$("#checkDate").val();
			var checkPlace=$("#checkPlace").val();
			var hoop=$("#hoop").val();
			var pregnant=$("#pregnant").val();
			var disease=$("#disease").val();
			var checkSuggest=$("#checkSuggest").val();
			$.ajax({
				type : "post",
				contentType : "application/x-www-form-urlencoded;charset=UTF-8",
				url : '/threeExaminations/threeCheckServiceController/save.do',
				async : false,
				data : {
					womanName:womanName,
					womanIdCard:womanIdCard,
					womanProvince:womanProvince,
					womanCity:womanCity,
					womanCounty:womanCounty,
					womanTown:womanTown,
					womanVallige:womanVallige,
					womanCurProvince:womanCurProvince,
					womanCurCity:womanCurCity,
					womanCurCounty:womanCurCounty,
					womanCurTown:womanCurTown,
					womanCurVallige:womanCurVallige,
					liveState:liveState,
					checkYear:checkYear,
					checkPatch:checkPatch,
					checkDate:checkDate,
					checkPlace:checkPlace,
					hoop:hoop,
					pregnant:pregnant,
					disease:disease,
					checkSuggest:checkSuggest
				},
				dataType : 'json',
				success : function(msg) {
					if(msg.result==true){
						alert("保存成功！");
						window.location.href='lookserverrecord.html';
					}else{
						alert(msg.message);
					}   
				},error: function(msg){
			        alert("网络超时！");
				}
			});
		}else{
			return true;
		}
	});
});
/**
 * 验证输入姓名
 */
function validateName(){
	if($("#womanName").val()==""){
		document.getElementById("fanWei").style.display = "none";
		document.getElementById("isNull").style.display = "block";
		return false;
	}else if(($("#womanName").val()).length>20){
		document.getElementById("isNull").style.display = "none";
		document.getElementById("fanWei").style.display = "block";
		return false;
	}else{
		document.getElementById("isNull").style.display = "none";
		document.getElementById("fanWei").style.display = "none";
		return true;
	}
}
function empty1(){
	document.getElementById("isNull").style.display = "none";
}
function empty2(){
	document.getElementById("idIsNull").style.display = "none";
}
function empty3(){
	document.getElementById("Valliage").style.display = "none";
}
function empty4(){
	document.getElementById("nowValliage").style.display = "none";
}
function empty5(){
	document.getElementById("dateIsNull").style.display = "none";
}
function empty6(){
	document.getElementById("checkPlaceIsNull").style.display = "none";
}
/**
 * 验证日期
 * @returns {Boolean}
 */
function validateDate(){
	if($("#checkDate").val()==""){
		document.getElementById("dateIsRight").style.display = "none";
		document.getElementById("dateIsNull").style.display = "block";
		return false;
	}else{
		var yourtime=$("#checkDate").val();
		yourtime = yourtime.replace("-","/");//替换字符，变成标准格式
		var d2=new Date();//取今天的日期
		var d1 = new Date(Date.parse(yourtime));
		if(d1>d2){
			document.getElementById("dateIsNull").style.display = "none";
			document.getElementById("dateIsRight").style.display = "block";
			return false;
		}else{
			document.getElementById("dateIsNull").style.display = "none";
			document.getElementById("dateIsRight").style.display = "none";
			return true;
		}
	}
}
/**
 * 验证检查地
 * @returns {Boolean}
 */
function validateCheckPlace(){
	if($("#checkPlace").val()==""){
		document.getElementById("checkPlaceIsRight").style.display = "none";
		document.getElementById("checkPlaceIsNull").style.display = "block";
		return false;
	}else if(($("#checkPlace").val()).length>65){
		document.getElementById("checkPlaceIsNull").style.display = "none";
		document.getElementById("checkPlaceIsRight").style.display = "block";
		return false;
	}else{
		document.getElementById("checkPlaceIsNull").style.display = "none";
		document.getElementById("checkPlaceIsRight").style.display = "none";
		return true;
	}
}
/**
 * 验证现居住村
 * @returns {Boolean}
 */
function validateNowvalliage(){
	if($("#womanCurVallige").val()==""){
		document.getElementById("nowValliageRight").style.display = "none";
		document.getElementById("nowValliage").style.display = "block";
		return false;
	}else if(($("#womanCurVallige").val()).length>30){
		document.getElementById("nowValliage").style.display = "none";
		document.getElementById("nowValliageRight").style.display = "block";
		return false;
	}else{
		document.getElementById("nowValliage").style.display = "none";
		document.getElementById("nowValliageRight").style.display = "none";
		return true;
	}
}
/**
 * 验证户籍地的村
 * @returns {Boolean}
 */
function validateValliage(){
	if($("#womanVallige").val()==""){
		document.getElementById("ValliageRight").style.display = "none";
		document.getElementById("Valliage").style.display = "block";
		return false;
	}else if(($("#womanVallige").val()).length>30){
		document.getElementById("Valliage").style.display = "none";
		document.getElementById("ValliageRight").style.display = "block";
		return false;
	}else{
		document.getElementById("Valliage").style.display = "none";
		document.getElementById("ValliageRight").style.display = "none";
		return true;
	}
}
function cheack(){
	if($("#womanName").val()==""){
		document.getElementById("isNull").style.display = "block";
		return false;
	}else if($("#womanIdCard").val()==""){
		document.getElementById("idIsNull").style.display = "block";
		return false;
	}else if(document.getElementById("idIsFemale").style.display == "block"){
		return false;
	}else if(document.getElementById("idIsRight").style.display == "block"){
		return false;
	}else if($("#womanVallige").val()==""){
		document.getElementById("Valliage").style.display = "block";
		return false;
	}else if($("#womanCurVallige").val()==""){
		document.getElementById("nowValliage").style.display = "block";
		return false;
	}else if($("#checkDate").val()==""){
		document.getElementById("dateIsNull").style.display = "block";
		return false;
	}else if($("#checkPlace").val()==""){
		document.getElementById("checkPlaceIsNull").style.display = "block";
		return false;
	}else{
		document.getElementById("isNull").style.display = "none";
		document.getElementById("idIsNull").style.display = "none";
		document.getElementById("Valliage").style.display = "none";
		document.getElementById("nowValliage").style.display = "none";
		document.getElementById("dateIsNull").style.display = "none";
		document.getElementById("checkPlaceIsNull").style.display = "none";
		return true;
	}
}
//妇女户籍地区选择
function womanProvinceChange() {
	var womanProvince = $("#womanProvince").val();
	findArea(womanProvince,"#womanCity");
	var womanCity = $("#womanCity").val();
	findArea(womanCity,"#womanCounty");
	var womanCounty = $("#womanCounty").val();
	findArea(womanCounty,"#womanTown");
};

function womanCityChange(){
	var womanCity = $("#womanCity").val();
	findArea(womanCity,"#womanCounty");
	var womanCounty = $("#womanCounty").val();
	findArea(womanCounty,"#womanTown");
};

function womanCountyChange(){
	var womanCounty = $("#womanCounty").val();
	findArea(womanCounty,"#womanTown");
};


//妇女现居地区选择
function womanCurProvinceChange() {
	var womanCurProvince = $("#womanCurProvince").val();
	findArea(womanCurProvince,"#womanCurCity");
	var womanCurCity = $("#womanCurCity").val();
	findArea(womanCurCity,"#womanCurCounty");
	var womanCurCounty = $("#womanCurCounty").val();
	findArea(womanCurCounty,"#womanCurTown");
};

function womanCurCityChange(){
	var womanCurCity = $("#womanCurCity").val();
	findArea(womanCurCity,"#womanCurCounty");
	var womanCurCounty = $("#womanCurCounty").val();
	findArea(womanCurCounty,"#womanCurTown");
};

function womanCurCountyChange(){
	var womanCurCounty = $("#womanCurCounty").val();
	findArea(womanCurCounty,"#womanCurTown");
};

function findArea(areaName,selectId){
	$.ajax({
		type : "post",
		contentType : "application/x-www-form-urlencoded;charset=UTF-8",
		url : '/threeExaminations/areaController/searchByAreaName.do',
		async : false,
		data : {
			areaName : areaName
		},
		dataType : 'json',
		success : function(msg) {
			if (msg.result == true) {
				var cityName="";
				
				$.each(msg.areaList,function(key,val){
					cityName+="<option>" + val.areaName + "</option>"; 
					});
				$(selectId).empty().append(cityName);
			} else {
				alert(msg.message);
			}
		},
		error : function(msg) {
			alert("网络超时！");
		}
	});
};
