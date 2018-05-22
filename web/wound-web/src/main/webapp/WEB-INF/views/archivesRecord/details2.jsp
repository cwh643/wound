<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<html>
<head>
	<title>榕创医疗</title>
</head>

<body>
<ul id="myTab" class="nav nav-tabs">
    <li class="active">
        <a href="#baseinfo" data-toggle="tab">
            基本信息
        </a>
    </li>
    <li><a href="#imageList" data-toggle="tab">照片</a></li>
    <li class="dropdown">
        <a href="#" id="myTabDrop1" class="dropdown-toggle"
           data-toggle="dropdown">档案
            <b class="caret"></b>
        </a>
        <ul class="dropdown-menu" role="menu" aria-labelledby="myTabDrop1">
            <li><a href="#record1" tabindex="-1" data-toggle="tab">伤口基本信息</a></li>
            <li><a href="#record2" tabindex="-1" data-toggle="tab">伤口的描述</a></li>
            <li><a href="#record3" tabindex="-1" data-toggle="tab">伤口评估</a></li>
            <li><a href="#record4" tabindex="-1" data-toggle="tab">渗液信息</a></li>
            <li><a href="#record5" tabindex="-1" data-toggle="tab">影响伤口愈合的因素</a></li>
            <li><a href="#record6" tabindex="-1" data-toggle="tab">伤口及周围组织的血液供应情况</a></li>
            <li><a href="#record7" tabindex="-1" data-toggle="tab">骨髓炎</a></li>
            <li><a href="#record8" tabindex="-1" data-toggle="tab">伤口换药</a></li>
        </ul>
    </li>
</ul>
<div id="myTabContent" class="tab-content">
    <div class="tab-pane fade in active" id="baseinfo">
    <form id="baseinfoForm" class="form-horizontal">
		<input type="hidden" name="id" value="${patient.id}"/>
		<input type="hidden" name="doctorId" value="${patient.doctorId}"/>
		<fieldset>
			
			<div class="control-group">
				<label class="control-label">姓名:</label>
				<div class="controls">
					<input type="text" name="name"  value="${patient.name}" class="input-large required" />
				</div>
			</div>
			<div class="control-group">
				<label class="control-label">性别:</label>
				<div class="controls">
				    <tags:radioGroup name="sex" dicmeta="${dict_sex}" value="${patient.sex}"  inline="inline"  />
				</div>
			</div>
			<div class="control-group">
				<label class="control-label">年龄:</label>
				<div class="controls">
					<input type="text" name="age"  value="${patient.age}" />
				</div>
			</div>
			<div class="control-group">
				<label class="control-label">床位号:</label>
				<div class="controls">
					<input type="text" name="bedNo"  value="${patient.bedNo}" />
				</div>
			</div>
			<div class="control-group">
				<label class="control-label">住院号:</label>
				<div class="controls">
					<input type="text" name="inpatientNo"  value="${patient.inpatientNo}" class="required" />
				</div>
			</div>
			<div class="control-group">
				<label class="control-label">入院诊断:</label>
				<div class="controls">
					<textarea name="diagnosis" class="input-large">${patient.diagnosis}</textarea>
				</div>
			</div>	
			<div class="form-actions">
				<a id="baseinfo_btn" class="btn btn-primary" href="javascript:void(0)">保存</a>
			</div>
		</fieldset>
	</form>
    </div>
    
    <div class="tab-pane fade" id="imageList">
		<div class="row-fluid">
		  <c:forEach items="${images}" var="img" varStatus="status">
		      <c:if test="${status.index % 4 == 0}">
		      <c:if test="${status.index > 0}">
		      	</ul>
		      </c:if>
		      <ul class="thumbnails">
		      </c:if>
			  <li class="span3">
		            <div class="thumbnail" style="height:200px;">
		                <a href="#">
		                    <div class="divcss5">
		                  	<img alt="" src="/imagebase/${img.imagePath}/list_rgb.jpeg"></img>
		                    </div>
		                </a>
		                <div class="caption">
		                    <div><h5 style="display: inline-block;"></h5></div>
		                  </div>
		            </div>
		        </li>
		    </c:forEach>
		    </ul>
		</div>
    </div>
    
    
    <div class="tab-pane fade" id="record1">
    <form id="record1Form" class="form-horizontal">
		<input type="hidden" name="id" value="${record.id}"/>
		<fieldset>
			<!-- <legend><small>基本信息</small></legend> -->
			<div class="control-group">
				<label class="control-label">伤口类型:</label>
				<div class="controls">
					<tags:dictSelect name="woundType" dicmeta="${dict_wound_type}" value="${record.woundType}"  />
				</div>
			</div>
			<div class="control-group">
				<label class="control-label">伤口尺寸:</label>
				<div class="controls">
					<label class="radio inline">
					  长<input type="text" name="woundWidth"  value="${record.woundWidth}" class="input-mini" />
					</label>
					<label class="radio inline">
					  宽<input type="text" name="woundHeight"  value="${record.woundHeight}" class="input-mini" />
					</label>
					<label class="radio inline">
					  深度<input type="text" name="woundDeep"  value="${record.woundDeep}" class="input-mini" />
					</label>
				</div>
				<div class="controls">
					<label class="radio inline">
					  面积<input type="text" name="woundArea"  value="${record.woundArea}" class="input-mini" />
					</label>
					<label class="radio inline">
					  容积<input type="text" name="woundVolume"  value="${record.woundVolume}" class="input-mini" />
					</label>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label">伤口形成时间:</label>
				<div class="controls">
					<div class="input-append date" id="datetimepicker">
					    <input class="span2" size="16" type="text" name="woundTime" value="${record.woundTime}">
					    <span class="add-on"><i class="icon-th"></i></span>
					</div>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label">目前对伤口采取的措施:</label>
				<div class="controls">
					<tags:dictSelect name="woundMeasures" dicmeta="${dict_wound_measures}" value="${record.woundMeasures}"  />
				</div>
			</div>
			<!-- 
			<div class="control-group">
				<label class="control-label">伤口对应的位置:</label>
				<div class="controls">
					<input type="text" name=""  value="" />
				</div>
			</div>	
			 -->
			<div class="form-actions">
				<a id="record1_btn" class="btn btn-primary" href="javascript:void(0)">保存</a>
			</div>
		</fieldset>
		</form>
    </div>
    <div class="tab-pane fade" id="record2">
        <form id="record2Form" class="form-horizontal">
		<input type="hidden" name="id" value="${record.id}"/>
		<fieldset>
			<!-- <legend><small>基本信息</small></legend> -->
			<div class="control-group">
				<label class="control-label">伤口清洁程度:</label>
				<div class="controls">
					<tags:radioGroup name="woundDescribeClean" dicmeta="${dict_wound_describe_clean}" value="${record.woundDescribeClean}"  inline="inline"  />
				</div>
			</div>
			<div class="control-group">
				<label class="control-label">伤口的基地颜色:</label>
				<div class="controls">
					<label class="radio inline">
					  红色组织(%)<input type="text" name="woundColorRed"  value="${record.woundColorRed}" class="input-mini" />
					</label>
					<label class="radio inline">
					  黄色组织(%)<input type="text" name="woundColorYellow"  value="${record.woundColorYellow}" class="input-mini" />
					</label>
					<label class="radio inline">
					  黑色组织(%)<input type="text" name="woundColorBlack"  value="${record.woundColorBlack}" class="input-mini" />
					</label>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label">伤口周围皮肤情况:</label>
				<div class="controls">
					<tags:radioGroup name="woundDescribeSkin" dicmeta="${dict_wound_describe_skin}" value="${record.woundDescribeSkin}"  inline="inline"  />
				</div>
			</div>
			<div class="form-actions">
				<a id="record2_btn" class="btn btn-primary" href="javascript:void(0)">保存</a>
			</div>
		</fieldset>
		</form>
    </div>
    <div class="tab-pane fade" id="record3">
        <form id="record3Form" class="form-horizontal">
		<input type="hidden" name="id" value="${record.id}"/>
		<fieldset>
			<!-- <legend><small>伤口属性</small></legend> -->
			<div class="control-group">
				<label class="control-label">伤口属性:</label>
				<div class="controls">
					<tags:radioGroup name="woundAssessProp" dicmeta="${dict_wound_assess_prop}" value="${record.woundAssessProp}"  inline="inline"  />
				</div>
			</div>
			<div class="control-group">
				<label class="control-label">伤口有无感染:</label>
				<div class="controls">
					<tags:radioGroup name="woundAssessInfect" dicmeta="${dict_wound_assess_infect}" value="${record.woundAssessInfect}"  inline="inline"  />
					<label class="radio inline">
					  <input type="text" name="woundAssessInfectDesc"  value="${wound.woundAssessInfectDesc}" />
					</label>
				</div>
			</div>
			<div class="form-actions">
				<a id="record3_btn" class="btn btn-primary" href="javascript:void(0)">保存</a>
			</div>
		</fieldset>
		</form>
    </div>
    <div class="tab-pane fade" id="record4">
        <form id="record4Form" class="form-horizontal">
		<input type="hidden" name="id" value="${record.id}"/>
		<fieldset>
			<!-- <legend><small>伤口属性</small></legend> -->
			<div class="control-group">
				<label class="control-label">伤口的渗液量:</label>
				<div class="controls">
					<tags:radioGroup name="woundLeachateVolume" dicmeta="${dict_wound_leachate_volume}" value="${record.woundLeachateVolume}"  inline="inline"  />
				</div>
			</div>
			<div class="control-group">
				<label class="control-label">渗液的颜色:</label>
				<div class="controls">
					<tags:radioGroup name="woundLeachateColor" dicmeta="${dict_wound_leachate_color}" value="${record.woundLeachateColor}"  inline="inline"  />
				</div>
			</div>
			<div class="control-group">
				<label class="control-label">渗液的色味:</label>
				<div class="controls">
					<tags:radioGroup name="woundLeachateSmell" dicmeta="${dict_wound_leachate_smell}" value="${record.woundLeachateSmell}"  inline="inline"  />
				</div>
			</div>
			<div class="form-actions">
				<a id="record4_btn" class="btn btn-primary" href="javascript:void(0)">保存</a>
			</div>
		</fieldset>
		</form>
    </div>
    <div class="tab-pane fade" id="record5">
        <form id="record5Form" class="form-horizontal">
		<input type="hidden" name="id" value="${record.id}"/>
		<fieldset>
			<!-- <legend><small>影响伤口愈合的因素</small></legend> -->
			<div class="control-group">
				<label class="control-label">全身因素:</label>
				<div class="controls">
					<tags:checkboxGroup name="woundHealAll" dicmeta="${dict_wound_heal_all}" value="${record.woundHealAll}"  inline="inline"  />
				</div>
			</div>
			<div class="control-group">
				<label class="control-label">局部因素:</label>
				<div class="controls">
					<tags:checkboxGroup name="woundHealPosition" dicmeta="${dict_wound_heal_position}" value="${record.woundHealPosition}"  inline="inline"  />
				</div>
			</div>
			<div class="form-actions">
				<a id="record5_btn" class="btn btn-primary" href="javascript:void(0)">保存</a>
			</div>
		</fieldset>
		</form>
    </div>
    <div class="tab-pane fade" id="record6">
        <form id="record6Form" class="form-horizontal">
		<input type="hidden" name="id" value="${record.id}"/>
		<fieldset>
			<!-- <legend><small>影响伤口愈合的因素</small></legend> -->
			<div class="control-group">
				<label class="control-label">热红外照片:</label>
				<div class="controls">
					<a class="btn" href="#">查看图片</a>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label">多普勒超声结果:</label>
				<div class="controls">
					<a class="btn" href="#">查看图片</a>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label">CT结果描述:</label>
				<div class="controls">
					<textarea name="woundCta" class="input-large">${record.woundCta}</textarea>
				</div>
			</div>
			<div class="form-actions">
				<a id="record6_btn" class="btn btn-primary" href="javascript:void(0)">保存</a>
			</div>
		</fieldset>
		</form>
    </div>
    <div class="tab-pane fade" id="record7">
        <form id="record7Form" class="form-horizontal">
		<input type="hidden" name="id" value="${record.id}"/>
		<fieldset>
			<!-- <legend><small>影响伤口愈合的因素</small></legend> -->
			<div class="control-group">
				<label class="control-label">伤口及伤口周围组织的核磁成像:</label>
				<div class="controls">
					<a class="btn" href="#">查看图片</a>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label">伤口及伤口周围组织的葡萄糖代谢显像（PETCT）:</label>
				<div class="controls">
					<a class="btn" href="#">查看图片</a>
				</div>
			</div>
			<div class="form-actions">
				<a id="record7_btn" class="btn btn-primary" href="javascript:void(0)">保存</a>
			</div>
		</fieldset>
		</form>
    </div>
    <div class="tab-pane fade" id="record8">
        <form id="record8Form" class="form-horizontal">
		<input type="hidden" name="id" value="${record.id}"/>
		<fieldset>
			<!-- <legend><small>影响伤口愈合的因素</small></legend> -->
			<div class="control-group">
				<label class="control-label">辅料材料描述:</label>
				<div class="controls">
					<textarea name="diagnosis" class="input-large">${userinfo.diagnosis}</textarea>
				</div>
			</div>
			<div class="form-actions">
				<a id="record8_btn" class="btn btn-primary" href="javascript:void(0)">保存</a>
			</div>
		</fieldset>
		</form>
    </div>

</div>


	<script>
		$(document).ready(function() {
			//聚焦第一个输入框
			$("#name").focus();
			//为inputForm注册validate函数
			$("#inputForm").validate();
			$('#datetimepicker').datetimepicker( {
				startView: 2,
				minView: 2,
			    format: 'yyyy-mm-dd',
				language: "zh-CN",
 				autoclose:true
			});
			
			$("#baseinfo_btn").on("click", submitBaseinfo);
			$("#record1_btn").on("click", submitRecord1);
			$("#record2_btn").on("click", submitRecord2);
			$("#record3_btn").on("click", submitRecord3);
			$("#record4_btn").on("click", submitRecord4);
			$("#record5_btn").on("click", submitRecord5);
			$("#record6_btn").on("click", submitRecord6);
		});
		
		function submitRecord1() {
			$('#record1Form').ajaxSubmit({
		        type:"post",   //post提交方式默认是get
		        dataType:"json",
		        url: "${ctx}/archivesRecord/updateRecord1",
		        beforeSubmit: function(){
					return $('#record1Form').valid();
				},
				success: function(result){
					if (result.success) {
						alert("保存成功");
					} else {
						alert("保存失败");
					}
				}
			});
		}
		
		function submitRecord2() {
			$('#record2Form').ajaxSubmit({
		        type:"post",   //post提交方式默认是get
		        dataType:"json",
		        url: "${ctx}/archivesRecord/updateRecord2",
		        beforeSubmit: function(){
					return $('#record2Form').valid();
				},
				success: function(result){
					if (result.success) {
						alert("保存成功");
					} else {
						alert("保存失败");
					}
				}
			});
		}
		
		function submitRecord3() {
			$('#record3Form').ajaxSubmit({
		        type:"post",   //post提交方式默认是get
		        dataType:"json",
		        url: "${ctx}/archivesRecord/updateRecord3",
		        beforeSubmit: function(){
					return $('#record3Form').valid();
				},
				success: function(result){
					if (result.success) {
						alert("保存成功");
					} else {
						alert("保存失败");
					}
				}
			});
		}
		
		function submitRecord4() {
			$('#record4Form').ajaxSubmit({
		        type:"post",   //post提交方式默认是get
		        dataType:"json",
		        url: "${ctx}/archivesRecord/updateRecord4",
		        beforeSubmit: function(){
					return $('#record4Form').valid();
				},
				success: function(result){
					if (result.success) {
						alert("保存成功");
					} else {
						alert("保存失败");
					}
				}
			});
		}
		
		function submitRecord5() {
			$('#record5Form').ajaxSubmit({
		        type:"post",   //post提交方式默认是get
		        dataType:"json",
		        url: "${ctx}/archivesRecord/updateRecord5",
		        beforeSubmit: function(){
					return $('#record5Form').valid();
				},
				success: function(result){
					if (result.success) {
						alert("保存成功");
					} else {
						alert("保存失败");
					}
				}
			});
		}
		
		function submitRecord6() {
			$('#record6Form').ajaxSubmit({
		        type:"post",   //post提交方式默认是get
		        dataType:"json",
		        url: "${ctx}/archivesRecord/updateRecord6",
		        beforeSubmit: function(){
					return $('#record6Form').valid();
				},
				success: function(result){
					if (result.success) {
						alert("保存成功");
					} else {
						alert("保存失败");
					}
				}
			});
		}
		
		function submitBaseinfo() {
			$('#baseinfoForm').ajaxSubmit({
		        type:"post",   //post提交方式默认是get
		        dataType:"json",
		        url: "${ctx}/archivesRecord/updatePatient",
		        beforeSubmit: function(){
					return $('#baseinfoForm').valid();
				},
				success: function(result){
					if (result.success) {
						alert("保存成功");
					} else {
						alert("保存失败");
					}
				}
			});
		}
	</script>
</body>
</html>
