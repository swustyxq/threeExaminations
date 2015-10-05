$(document).ready(function(){	
	publicYearList = new Array();;
	yearNum = 0;
	//修改时，显示某一选中的任务原信息
	var Request = new Object();
	Request = GetRequest();
	var taskId ;
	taskId = Request['id'];
	$.ajax({
		type : "post",
		contentType : "application/x-www-form-urlencoded;charset=UTF-8",
		url : '/threeExaminations/taskController/showTaskContent.do',
		async : false,
		data : {
			taskId:taskId
		},
		dataType : 'json',
		success : function(msg) {
			setTaskYear();//设置年度为今年和明年
			if(msg.result ==true){
				//var title = "";
				var year = "";
				var batch ="";
				var startTime = "";
				var endTime = "";
				var content = "";
				year += msg.task.taskYear;
				batch += msg.task.taskBatch;
				startTime += msg.startTime;
				endTime += msg.endTime;
				content += msg.task.taskContent;
				
				var flag = 0;
				for(var i=0;i < yearNum; i++){
					if(year == publicYearList[i]){
						flag = 1;
						break;
					}
				}
				if(flag == 0){//任务时间不是今年或明年
					$("#year").append("<option>"+year+"</option>");
				}
				$("#year").val(year);
				$("#batch").val(batch);
				$("#startTime").val(startTime);
				$("#endTime").val(endTime);
				$("#content").val(content);
			}else{
				alert(msg.message);
			};
		},error: function(msg){
	        alert("网络超时！");
		}
	});
	//保存修改的任务信息
	$("#edit").click(function(){
		var year = $("#year").val(); 
		var batch = $("#batch").val();
		//var title = $("#title").val();
		var startTime = $("#startTime").val();
		var endTime = $("#endTime").val();
		var content = $("#content").val();
		if(checkTask()){
		$.ajax({
			type : "post",
			contentType : "application/x-www-form-urlencoded;charset=UTF-8",
			url : '/threeExaminations/taskController/editTask.do',
			async : false,
			data : {
				taskId:taskId,
				year:year,
				batch:batch,
				startTime:startTime,
				endTime:endTime,
				content:content
			},
			dataType : 'json',
			success : function(msg) {
				if(msg.result == true){
					//alert("修改成功！");
					document.getElementById('warn').innerHTML = "修改成功!";
					$('#myWarnModal').modal('show');
					$("#sure").click(function(){
					window.location.href='taskmanagement.html';
					});
				}
				else{
					//alert(msg.message);
					document.getElementById('warn').innerHTML = msg.message;
					$('#myWarnModal').modal('show');
				}
			},error: function(msg){
		        alert("网络超时！");
			}
		});
		}
	});
	
	//发布一条新的公告信息
	$("#addNew").click(function(){
		var year = $("#year").val(); 
		var flag = 0;
		for(var i = 0; i < yearNum; i++){
			if(year == publicYearList[i]){
				flag = 1;
				break;
			}
		}
		if(flag == 0){
			document.getElementById('warn').innerHTML = "任务年度只能是今年或明年！";
			$('#myWarnModal').modal('show');
			return;
		}
		var batch = $("#batch").val();
		//var title = $("#title").val();
		var startTime = $("#startTime").val();
		var endTime = $("#endTime").val();
		var content = $("#content").val();
		if(checkTask()){
		$.ajax({
			type : "post",
			contentType : "application/x-www-form-urlencoded;charset=UTF-8",
			url : '/threeExaminations/taskController/publishSurveyTask.do',
			async : false,
			data : {
				year:year,
				batch:batch,
				//title:title,
				startTime:startTime,
				endTime:endTime,
				content:content
			},
			dataType : 'json',
			success : function(msg) {
				if(msg.result == true){
					//alert("发布成功！");
					document.getElementById('warn').innerHTML = "发布成功！";
					$('#myWarnModal').modal('show');
					$("#sure").click(function(){
					window.location.href='taskmanagement.html';
					});
				}else{
					//alert(msg.message);
					document.getElementById('warn').innerHTML = msg.message;
					$('#myWarnModal').modal('show');
				}
			},error: function(msg){
		        alert("网络超时！");
			}
		});
		}
	});
});

//获取系统时间，设置任务的年度只能为今年和明年
function setTaskYear(){
	var timestamp = new Date();
	var nowYear = timestamp.getFullYear();
	var nextYear = timestamp.getFullYear() + 1;
	var year="<option>"+nowYear+"</option>"
              +"<option>"+nextYear+"</option>";
    $("#year").empty().append(year);
	publicYearList[yearNum++] = nowYear;
	publicYearList[yearNum++] = nextYear;
}

function checkTask(){
	if($("#year").val()==""){
		document.getElementById("checkYear").style.display="block";
		document.getElementById("year").focus();
	}else if($("#startTime").val()==""){
		document.getElementById("checkStartTime").style.display="block";
		document.getElementById("startTime").focus();
		return false;
	}else if($("#endTime").val()==""){
		document.getElementById("checkEndTime1").style.display="block";
		document.getElementById("checkEndTime2").style.display="none";
		document.getElementById("endTime").focus();
		return false;
	}else if($("#content").val()==""){
	    document.getElementById("checkTaskContent1").style.display="block";
	    document.getElementById("checkTaskContent2").style.display="none";
	    document.getElementById("content").focus();
	    return false;
    }else if(($("#content").val()).length > 5000){
	    document.getElementById("checkTaskContent1").style.display="none";
	    document.getElementById("checkTaskContent2").style.display="block";
	    document.getElementById("content").focus();
	    return false;
    }else if($("#endTime").val()!=""){
		var startTime = $("#startTime").val();
		startTime = startTime.replace("-","/");//替换字符，变成标准格式
		var endTime = $("#endTime").val();
		endTime=endTime.replace("-","/");//替换字符，变成标准格式
		if(endTime < startTime){
			document.getElementById("checkEndTime1").style.display="none";
			document.getElementById("checkEndTime2").style.display="block";
			document.getElementById("endTime").focus();
			return false;
		}else{
			return true;
		}
	}else{
		return true;
	}
}

function GetRequest() {
	var url = location.search; //获取url中"?"符后的字串
	var theRequest = new Object();
	if (url.indexOf("?") != -1) { 
		var str = url.substr(1);
		strs = str.split("&");
		for(var i = 0; i < strs.length; i ++) { 
			theRequest[strs[i].split("=")[0]]=unescape(strs[i].split("=")[1]);
			}
		}
	return theRequest;
}

//即时验证任务信息
function checkTaskYear(){
	if($("#year").val()==""){
		document.getElementById("checkYear").style.display="block";
		//document.getElementById("year").focus();
	}else{
	}
}

function checkTaskBatch(){
	if($("#batch").val()==""){
		document.getElementById("checkBatch").style.display="block";
		//document.getElementById("batch").focus();
	}else{
		document.getElementById("checkBatch").style.display="none";
	}
}

function checkTaskStartTime(){
	if($("#startTime").val()==""){
		document.getElementById("checkStartTime").style.display="block";
		//document.getElementById("startTime").focus();
	}else{
		document.getElementById("checkStartTime").style.display="none";
	}
}

function checkTaskEndTime(){
	if($("#endTime").val()==""){
		document.getElementById("checkEndTime1").style.display="block";
		document.getElementById("checkEndTime2").style.display="none";
		//document.getElementById("endTime").focus();
		return false;
	}else{
		var startTime = $("#startTime").val();
		startTime = startTime.replace("-","/");//替换字符，变成标准格式
		var endTime = $("#endTime").val();
		endTime=endTime.replace("-","/");//替换字符，变成标准格式
		if(endTime < startTime){
			document.getElementById("checkEndTime1").style.display="none";
			document.getElementById("checkEndTime2").style.display="block";
			//document.getElementById("endTime").focus();
		}else{
			document.getElementById("checkEndTime1").style.display="none";
			document.getElementById("checkEndTime2").style.display="none";
		}
	}
}

function checkTaskContent(){
	if($("#content").val()==""){
		document.getElementById("checkTaskContent1").style.display="block";
		document.getElementById("checkTaskContent2").style.display="none";
		//document.getElementById("content").focus();
	}else if(($("#content").val()).length > 5000){
		document.getElementById("checkTaskContent1").style.display="none";
		document.getElementById("checkTaskContent2").style.display="block";
		document.getElementById("content").focus();
	}else{
		document.getElementById("checkTaskContent1").style.display="none";
		document.getElementById("checkTaskContent2").style.display="none";
	}
}