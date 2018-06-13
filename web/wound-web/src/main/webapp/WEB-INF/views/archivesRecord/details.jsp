<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<html>
<head>
	<title>榕创医疗</title>
	<style>
	<!--
	.bs-docs-example {
	    position: relative;
	    margin: 15px 0;
	    padding: 39px 19px 14px;
	    background-color: #fff;
	    border: 1px solid #ddd;
	    -webkit-border-radius: 4px;
	    -moz-border-radius: 4px;
	    border-radius: 4px;
	}
	.bs-docs-example .title {
	    position: absolute;
	    top: -1px;
	    left: -1px;
	    padding: 3px 7px;
	    font-size: 12px;
	    font-weight: bold;
	    background-color: #f5f5f5;
	    border: 1px solid #ddd;
	    color: #9da0a4;
	    -webkit-border-radius: 4px 0 4px 0;
	    -moz-border-radius: 4px 0 4px 0;
	    border-radius: 4px 0 4px 0;
	}
	
	.bs-docs-sidenav {
	    width: 228px;
	    margin: 30px 0 0;
	    padding: 0;
	    background-color: #fff;
	    -webkit-border-radius: 6px;
	    -moz-border-radius: 6px;
	    border-radius: 6px;
	    -webkit-box-shadow: 0 1px 4px rgba(0,0,0,.065);
	    -moz-box-shadow: 0 1px 4px rgba(0,0,0,.065);
	    box-shadow: 0 1px 4px rgba(0,0,0,.065);
	}
	
	.bs-docs-sidenav.affix {
	    top: 65px;
	}
	
	.bs-docs-sidenav > li > a:hover {
	    background-color: #f5f5f5;
	}
	
	.bs-docs-sidenav > li:first-child > a {
	    -webkit-border-radius: 6px 6px 0 0;
	    -moz-border-radius: 6px 6px 0 0;
	    border-radius: 6px 6px 0 0;
	}
	
	.bs-docs-sidenav > li:last-child > a {
	    -webkit-border-radius: 0 0 6px 6px;
	    -moz-border-radius: 0 0 6px 6px;
	    border-radius: 0 0 6px 6px;
	}
	
	.bs-docs-sidenav > li > a {
	    display: block;
	    width: 190px \9;
	    margin: 0 0 -1px;
	    padding: 8px 14px;
	    border: 1px solid #e5e5e5;
	}
	
	.bs-docs-sidenav a:hover .icon-chevron-right {
	    opacity: .5;
	}
	
	.bs-docs-sidenav .icon-chevron-right {
	    float: right;
	    margin-top: 2px;
	    margin-right: -6px;
	    opacity: .25;
	}
	
	#container-line {
		min-width: 310px;
		max-width: 800px;
		height: 400px;
		margin: 0 auto
	}
	#container-column {
		min-width: 310px; 
		height: 400px; 
		margin: 0 auto
	}
	#popMenu {
		
	}
	
	.title1 {
	    height:60px;
	    width:100%;
		background: url(${ctx}/static/images/title_bar.png) no-repeat top left;
	}
	
	.title1 > span {
	    color: #ffffff;
	    font-size: 16px;
	    line-height: 40px;
	    height: 40px;
	    margin-left: 60px;
	}
	.title1 > i {
	    background: url(${ctx}/static/images/bar_save_button.png) no-repeat top left;
	    width: 30px;
	    height: 30px;
	    display: inline-block;
	    margin-top: 5px;
	    line-height: 30px;
	    float: right;
	    margin-right: 10px;
	}
	-->
	</style>
	<script src="${ctx}/static/Highcharts-6.0.1/code/highcharts.js"></script>
</head>

<body>

<div class="container-fluid">
  <div class="row-fluid">
    <div class="span3 bs-docs-sidebar">
       <ul class="nav nav-list bs-docs-sidenav affix">
          <li ><a href="#baseinfo"><i class="icon-chevron-right"></i>患者基本信息</a></li>
          <li ><a href="#photo"><i class="icon-chevron-right"></i> 照片</a></li>
          <li ><a href="#record1"><i class="icon-chevron-right"></i> 伤口基本信息</a></li>
          <li ><a href="#history"><i class="icon-chevron-right"></i> 历史数据</a></li>
          <!-- <li ><a href="#record2"><i class="icon-chevron-right"></i> 伤口位置</a></li> -->
          <li ><a href="#record3"><i class="icon-chevron-right"></i> 伤口的描述</a></li>
          <li ><a href="#record4"><i class="icon-chevron-right"></i> 伤口评估</a></li>
          <li ><a href="#record5"><i class="icon-chevron-right"></i> 渗液信息</a></li>
          <li ><a href="#record6"><i class="icon-chevron-right"></i> 影响伤口愈合的因素</a></li>
          <li ><a href="#record7"><i class="icon-chevron-right"></i> 辅助检查</a></li>
          <li ><a href="#record8"><i class="icon-chevron-right"></i> 骨髓炎</a></li>
          <li ><a href="#record9"><i class="icon-chevron-right"></i> 伤口换药</a></li>
        </ul>
        <a class="btn  btn-primary affix" id="back_btn" style="top:520px;" href="javascript:history.back();">返回</a>
    </div>
    <div class="span9">
	    <div class="bs-docs-example"  id="baseinfo">
	      <div class="title1"><span>基本信息</span><i id="baseinfo_btn" style="cursor:pointer;"></i></div>
		    <form id="baseinfoForm" class="form-horizontal">
				<input type="hidden" name="id" value="${patient.id}"/>
				<input type="hidden" name="doctorId" value="${patient.doctorId}"/>
				<fieldset>
					<div class="row">
						<div class="control-group span5">
							<label class="control-label">姓名:</label>
							<div class="controls">
								<input type="text" name="name"  value="${patient.name}" class=" required" />
							</div>
						</div>
						<div class="control-group span5">
							<label class="control-label">性别:</label>
							<div class="controls">
							    <tags:radioGroup name="sex" dicmeta="${dict_sex}" value="${patient.sex}"  inline="inline"  />
							</div>
						</div>
					</div>
					
					<div class="row">
						<div class="control-group span5">
							<label class="control-label">年龄:</label>
							<div class="controls">
								<input type="text" name="age"  value="${patient.age}" />
							</div>
						</div>
						<div class="control-group span5">
							<label class="control-label">床位号:</label>
							<div class="controls">
								<input type="text" name="bedNo"  value="${patient.bedNo}" />
							</div>
						</div>
		
					</div>
					
					<div class="row">
						<div class="control-group span5">
							<label class="control-label">住院号:</label>
							<div class="controls">
								<input type="text" name="inpatientNo"  id="patient_inpatientNo" value="${patient.inpatientNo}" class="required" />
							</div>
						</div>
						<div class="control-group span5">
							<label class="control-label">入院诊断:</label>
							<div class="controls">
								<textarea name="diagnosis" class="input-large">${patient.diagnosis}</textarea>
							</div>
						</div>	
					</div>
					<!-- 
					<div class="form-actions">
						<a id="baseinfo_btn" class="btn btn-primary" href="javascript:void(0)">更新</a>
					</div>
					 -->
				</fieldset>
			</form>
	    </div>
    
     <div class="bs-docs-example"  id="photo">
      <div class="title1"><span>照片</span></div>
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
		                <a href="${ctx}/static/wound/${img.imagePath}/list_rgb.jpeg" target="_blank">
		                    <div class="divcss5" style="margin-top: 30px">
		                  	<img alt="" src="${ctx}/static/wound/${img.imagePath}/list_rgb.jpeg"></img>
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
     
     <div class="bs-docs-example" id="record1">
      <div class="title1"><span>伤口基本信息</span><i id="record1_btn" style="cursor:pointer;"></i></div>
	    <form id="record1Form" class="form-horizontal">
			<input type="hidden" name="id" value="${record.id}"/>
			<fieldset>
				<!-- <legend><small>基本信息</small></legend> -->
				<div class="control-group">
					<label class="control-label">伤口类型:</label>
					<div class="controls">
						<tags:dictSelect name="woundType" dicmeta="${dict_wound_type}" value="${record.woundType}"  />
						<label class="radio inline">
						  <input type="text" name="woundTypeDesc"  value="${record.woundTypeDesc}"  placeholder="其他类型描述" />
						</label>
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
						    <input size="16" type="text" name="woundTime" value="${record.woundTime}">
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
				<div class="control-group"  id="record2">
					<label class="control-label">伤口对应的位置:</label>
					<div class="controls">
						<input type="text" name=""  value="" />
					</div>
				</div>	
				 -->
				  <!-- 
				<div class="form-actions">
					<a id="record1_btn" class="btn btn-primary" href="javascript:void(0)">更新</a>
				</div>
				 -->
			</fieldset>
		</form>
    </div>
    <!-- 
    <div class="bs-docs-example">
      <span class="title">伤口位置</span>
    </div>
     -->
    <div class="bs-docs-example" id="history">
      <div class="title1"><span>历史数据</span><i></i></div>
      <div class="row" style=" margin-left: 0px;">
      	<div class="span5 ">
      		<div id="container-column"></div>
      	</div>
      	<div class="span5  offset1">
      		<div id="container-line"></div>
      	</div>
      </div>
    </div>
    <div class="bs-docs-example"  id="record3">
      <div class="title1"><span>伤口的描述</span><i id="record2_btn" style="cursor:pointer;"></i></div>
        <form id="record2Form" class="form-horizontal">
		<input type="hidden" name="id" value="${record.id}"/>
		<fieldset>
			<!-- <legend><small>基本信息</small></legend> -->
			<div class="control-group">
				<label class="control-label">伤口清洁程度:</label>
				<div class="controls">
					<tags:radioGroup name="woundDescribeClean" dicmeta="${dict_wound_describe_clean}" value="${record.woundDescribeClean}"  inline="inline"  />
					<label class="radio inline">
					  <input type="text" name="woundAssessInfectDesc"  value="${record.woundAssessInfectDesc}" placeholder="感染的细菌种类，药物试验" />
					</label>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label">伤口的基底颜色:</label>
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
				    <tags:dictSelect name="woundDescribeSkin" dicmeta="${dict_wound_describe_skin}" value="${record.woundDescribeSkin}"  />
				</div>
			</div>
			<!-- 
			<div class="form-actions">
				<a id="record2_btn" class="btn btn-primary" href="javascript:void(0)">更新</a>
			</div>
			 -->
		</fieldset>
		</form>
    </div>
    
    <div class="bs-docs-example"  id="record4">
      <div class="title1"><span>伤口评估</span><i id="record3_btn" style="cursor:pointer;"></i></div>
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
			<%--
			<div class="control-group">
				<label class="control-label">伤口有无感染:</label>
				<div class="controls">
					<tags:radioGroup name="woundAssessInfect" dicmeta="${dict_wound_assess_infect}" value="${record.woundAssessInfect}"  inline="inline"  />
					<label class="radio inline">
					  <input type="text" name="woundAssessInfectDesc"  value="${wound.woundAssessInfectDesc}" />
					</label>
				</div>
			</div>
			 --%>
			<div class="control-group">
				<label class="control-label">伤口疼痛等级:</label>
				<div class="controls">
					<tags:dictSelect name="woundAche" dicmeta="${dict_wound_ache}" value="${record.woundAche}"  />
				</div>
			</div>
			<!-- 
			<div class="form-actions">
				<a id="record3_btn" class="btn btn-primary" href="javascript:void(0)">更新</a>
			</div>
			-->
		</fieldset>
		</form>
    </div>
    
    <div class="bs-docs-example"  id="record5">
      <div class="title1"><span>渗液信息</span><i id="record4_btn" style="cursor:pointer;"></i></div>
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
			<!-- 
			<div class="form-actions">
				<a id="record4_btn" class="btn btn-primary" href="javascript:void(0)">更新</a>
			</div>
			-->
		</fieldset>
		</form>
    </div>
    
    <div class="bs-docs-example"  id="record6">
      <div class="title1"><span>影响伤口愈合的因素</span><i id="record5_btn" style="cursor:pointer;"></i></div>
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
			<!-- 
			<div class="form-actions">
				<a id="record5_btn" class="btn btn-primary" href="javascript:void(0)">更新</a>
			</div>
			-->
		</fieldset>
		</form>
    </div>
    
    <div class="bs-docs-example"  id="record7">
      <div class="title1"><span>辅助检查</span><i id="record6_btn" style="cursor:pointer;"></i></div>
        <form id="record6Form" class="form-horizontal">
		<input type="hidden" name="id" value="${record.id}"/>
		<fieldset>
			<!-- <legend><small>影响伤口愈合的因素</small></legend> -->
			<div class="control-group">
				<label class="control-label">热红外照片:</label>
				<div class="controls">
					<!--<a class="btn" href="#" target="_blank">查看图片</a>-->
				</div>
			</div>
			<div class="control-group">
				<label class="control-label">多普勒超声结果:</label>
				<div class="controls">

					<c:if test="${not empty record.woundDoppler}">
						<c:forEach var="v" items="${fn:split(record.woundDoppler,';')}">
							<a class="btn" href="${ctx}/static/wound/${record.uuid}/${v}" target="_blank">查看图片</a>
						</c:forEach>
					</c:if>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label">CT结果描述:</label>
				<div class="controls">
					<c:if test="${not empty record.woundCta}">
						<c:forEach var="v" items="${fn:split(record.woundCta,';')}">
							<a class="btn" href="${ctx}/static/wound/${record.uuid}/${v}" target="_blank">查看图片</a>
						</c:forEach>
					</c:if>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label">常规检查结果:</label>
				<div class="controls">
					<c:if test="${not empty record.woundExam}">
						<c:forEach var="v" items="${fn:split(record.woundExam,';')}">
							<a class="btn" href="${ctx}/static/wound/${record.uuid}/${v}" target="_blank">查看图片</a>
						</c:forEach>
					</c:if>
				</div>
			</div>
			<!-- 
			<div class="form-actions">
				<a id="record6_btn" class="btn btn-primary" href="javascript:void(0)">更新</a>
			</div>
			-->
		</fieldset>
		</form>
    </div>
    
    <div class="bs-docs-example"  id="record8">
       <div class="title1"><span>骨髓炎</span><i id="record7_btn" style="cursor:pointer;"></i></div>
        <form id="record7Form" class="form-horizontal">
		<input type="hidden" name="id" value="${record.id}"/>
		<fieldset>
			<!-- <legend><small>影响伤口愈合的因素</small></legend> -->
			<div class="control-group">
				<label class="control-label">伤口及伤口周围组织的核磁成像:</label>
				<div class="controls">
					<c:if test="${not empty record.woundMr}">
						<c:forEach var="v" items="${fn:split(record.woundMr,';')}">
							<a class="btn" href="${ctx}/static/wound/${record.uuid}/${v}" target="_blank">查看图片</a>
						</c:forEach>
					</c:if>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label">伤口及伤口周围组织的葡萄糖代谢显像（PETCT）:</label>
				<div class="controls">
					<c:if test="${not empty record.woundPetct}">
						<c:forEach var="v" items="${fn:split(record.woundPetct,';')}">
							<a class="btn" href="${ctx}/static/wound/${record.uuid}/${v}" target="_blank">查看图片</a>
						</c:forEach>
					</c:if>
				</div>
			</div>
			<!-- 
			<div class="form-actions">
				<a id="record7_btn" class="btn btn-primary" href="javascript:void(0)">更新</a>
			</div>
			-->
		</fieldset>
		</form>
    </div>
    
    <div class="bs-docs-example"  id="record9">
      <div class="title1"><span>伤口换药</span><i id="record8_btn" style="cursor:pointer;"></i></div>
      <form id="record8Form" class="form-horizontal">
		<input type="hidden" name="id" value="${record.id}"/>
		<fieldset>
			<!-- <legend><small>影响伤口愈合的因素</small></legend> -->
			<div class="control-group">
				<label class="control-label">敷料材料描述:</label>
				<div class="controls">
					<tags:dictSelect name="woundDressingType" dicmeta="${dict_wound_dressing_type}" value="${record.woundDressingType}"  />
				</div>
			</div>
			<div class="row">
			  <div class="control-group span5">
				<label class="control-label">敷料名称:</label>
				<div class="controls">
					<input type="text"  name="woundDressing1" value="${record.woundDressing1}" />
				</div>
			</div>
			<div class="control-group span5">
				<label class="control-label">敷料规格:</label>
				<div class="controls">
					<input type="text"  name="woundDressing2" value="${record.woundDressing2}" />
				</div>
			</div>
			</div>
			<div class="control-group">
				<label class="control-label">敷料简介:</label>
				<div class="controls">
					<label id="woundDressingDesc" style="width: 90%;height: 100px;">主要由聚氨酯类材料和脱敏医用粘胶组成，分内外两层，内层为未水性材料，可吸收创面渗液，外层材料具有良好的透气性和弹性。

此类敷料的特点是透明，便于观察伤口，能密切黏附于创面表面，有效保持创面渗出液，从而提供有利于创面愈合的湿润环境，促使坏死组织脱落；

缺点为该类敷料吸水性能欠佳，吸收饱和后易致膜下渗液积聚，可能诱发或加重感染，故只适用于相对清洁创面，不适于渗液多的创面。

有临床研究表明[1]，在气管切开护理及中心静脉置管的维护中应用该类敷料的疗效较好，不但能有效防止感染，同时还能改善患者的舒适，提高生质量值得临床推广应用。</label>
				</div>
			</div>
			<!-- 
			<div class="form-actions">
				<a id="record8_btn" class="btn btn-primary" href="javascript:void(0)">更新</a>
			</div>
			-->
		</fieldset>
		</form>
    </div>
    </div>
  </div>
</div>

<div id="myTabContent" class="tab-content">
    <div id="popMenu"></div>


</div>


	<script>
	
	var desc_arry = [
        "主要由聚氨酯类材料和脱敏医用粘胶组成，分内外两层，内层为未水性材料，可吸收创面渗液，外层材料具有良好的透气性和弹性。" +
                "此类敷料的特点是透明，便于观察伤口，能密切黏附于创面表面，有效保持创面渗出液，从而提供有利于创面愈合的湿润环境，促使坏死组织脱落；" +
                "缺点为该类敷料吸水性能欠佳，吸收饱和后易致膜下渗液积聚，可能诱发或加重感染，故只适用于相对清洁创面，不适于渗液多的创面。" +
                "有临床研究表明[1]，在气管切开护理及中心静脉置管的维护中应用该类敷料的疗效较好，不但能有效防止感染，同时还能改善患者的舒适，提高生质量值得临床推广应用。"
        , 
        "是将水溶性高分子材料或其单体经特殊加工形成的一种具有三维网状结构且不溶于水的胶状物质，主要成分为纯水70%-90%、羧甲基纤维索及其他一些附加成分。" +
                "有临床报道指出[2]：水凝胶敷料能与不平整的创面紧密黏合，减少细菌滋生的机会，防止创面感染，加速新生血管生成，促进上皮细胞生长。水凝胶的主要作用为自体清创，机制是在湿润环境中依靠创面自身渗出液中的胶原蛋白降解酶分解坏死物质。"
        , 
        "水胶体类敷料是由亲水胶微粒的明胶、果胶和羧甲基纤维素混合形成。主要适应于Ⅰ、Ⅱ期压疮的预防与治疗，烧伤、整形供皮区的治疗[3]，各类浅表外伤口和整形美容伤口的治疗，慢性伤口上皮形成期及静脉炎的预防与治疗等[4]。"
         ,
        "新型的泡沫类敷料外层为疏水材料，内层为亲水材料。此类敷料具有多孔性，表面张力低，富有弹性，可塑性强、轻便，对渗出液的吸收力可达到敷料本身质量的10倍。" +
                "泡沫性敷料对创面渗出物的处理是靠水蒸汽的转运和吸收机制来控制的，可塑性好，能制成各种厚度，对创面具有较好的保护作用。" +
                "目前使用最多的材料是聚氨酯泡沫和聚乙烯醇泡沫。其缺点是粘贴性较差，而需外固定材料；敷料不透明，难以观察创面情况；敷料孔隙大，创面肉芽组织易于长入，造成脱膜困难， 而且易受细菌污染[5]。"
         , 
        "主要成分取自海水中的藻类，它是利用藻类中类似纤维素的不能溶解的多糖藻酸盐制成的敷料。海藻酸可与金属离子结合形成盐，是各种金属离子的良好载体，常见的有藻酸钙盐、藻酸锌盐敷料。" +
                "藻酸钙盐敷料在更换过程中不造成新鲜肉芽损伤引起的疼痛，易被患者接受，还可以通过钙、钠离子交换，达到止血功能，同时还具有吸附细菌，阻止细菌进入创面的功能[6]。研究显示，藻酸锌盐敷料具有很好的凝血效应和增强血小板活性的作用。[7]"
        ,
         "即用浸渍或涂敷方法将药物涂覆于敷料上，如软膏类敷料、消毒敷料，以及中药敷料等，有保护创面、止痛、止血、消炎、促进新生肉芽组织及上皮细胞生长，加速创面愈合等功能。例如：具有抗菌作用的磺胺嘧啶银敷料、二氧化钛抗菌敷料，具有消炎作用的利多卡因敷料，具有快速祛痛、止血消炎功能的中草药敷料。"
        ,
         "其他"
	];
	
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
			$("#record8_btn").on("click", submitRecord8);
			
			$("select[name='woundDressingType']").on("change", onWoundDressingTypeChange);
			woundDressingTypeChange($("select[name='woundDressingType']").val());
			
			loadHistoryData();
		});
		
		function woundDressingTypeChange(val) {
			if (val) {
				$('#woundDressingDesc').text(desc_arry[val - 1]);
			}
		}
		
		function onWoundDressingTypeChange() {
			var val = $(this).val();
			$('#woundDressingDesc').text(desc_arry[val - 1]);
		}
		
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
						alert("更新成功");
					} else {
						alert("更新失败");
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
						alert("更新成功");
					} else {
						alert("更新失败");
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
						alert("更新成功");
					} else {
						alert("更新失败");
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
						alert("更新成功");
					} else {
						alert("更新失败");
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
						alert("更新成功");
					} else {
						alert("更新失败");
					}
				}
			});
		}
		
		function submitRecord8() {
			$('#record8Form').ajaxSubmit({
		        type:"post",   //post提交方式默认是get
		        dataType:"json",
		        url: "${ctx}/archivesRecord/updateRecord8",
		        beforeSubmit: function(){
					return $('#record8Form').valid();
				},
				success: function(result){
					if (result.success) {
						alert("更新成功");
					} else {
						alert("更新失败");
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
						alert("更新成功");
					} else {
						alert("更新失败");
					}
				}
			});
		}
		
		function loadHistoryData() {
			var inpatientNo = $("#patient_inpatientNo").val();
			$.get("${ctx}/archivesRecord/queryHistroy", {"inpatientNo": inpatientNo}, function(ret) {
				if (ret.success) {
					loadLineChart(ret.lineData);
					loadColumnBar(ret.barData);
				} else {
					alert(ret.message);
				}
			});
		}
		
		function loadLineChart(lineData) {
			Highcharts.chart('container-line', {

			    title: {
			        text: ''
			    },

			    subtitle: {
			        text: ''
			    },
			    xAxis: {
			        categories: lineData.categories
			    },
			    yAxis: {
			        title: {
			            text: ''
			        }
			    },
			    legend: {
			        layout: 'vertical',
			        align: 'right',
			        verticalAlign: 'middle'
			    },
/*
			    plotOptions: {
			        series: {
			            label: {
			                connectorAllowed: false
			            },
			            pointStart: 2010
			        }
			    },
*/
/*
			    series: [{
			        name: '宽度',
			        data: [43934, 52503, 57177, 69658, 97031, 119931, 137133, 154175]
			    }, {
			        name: '长度',
			        data: [24916, 24064, 29742, 29851, 32490, 30282, 38121, 40434]
			    }],
*/
				series: lineData.series,
				
			    responsive: {
			        rules: [{
			            condition: {
			                maxWidth: 500
			            },
			            chartOptions: {
			                legend: {
			                    layout: 'horizontal',
			                    align: 'center',
			                    verticalAlign: 'bottom'
			                }
			            }
			        }]
			    }

			});
		}
		
		function loadColumnBar(barData) {
			Highcharts.chart('container-column', {
			    chart: {
			        type: 'column'
			    },
			    title: {
			        text: ''
			    },
			    subtitle: {
			        text: ''
			    },
			    xAxis: {
			        categories: barData.categories,
			        crosshair: true
			    },
			    yAxis: {
			        min: 0,
			        title: {
			            text: '(%)'
			        }
			    },
			    tooltip: {
			        headerFormat: '<span style="font-size:10px">{point.key}</span><table>',
			        pointFormat: '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' +
			            '<td style="padding:0"><b>{point.y:.1f} %</b></td></tr>',
			        footerFormat: '</table>',
			        shared: true,
			        useHTML: true
			    },
			    plotOptions: {
			        column: {
			            pointPadding: 0.2,
			            borderWidth: 0
			        }
			    },
			    series: barData.series
			});
		}
		

		
	</script>
</body>
</html>
