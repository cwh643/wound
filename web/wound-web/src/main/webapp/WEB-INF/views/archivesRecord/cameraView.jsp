<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<html>
<head>
	<title>榕创医疗</title>
	<meta charset="UTF-8">
	<style>
		body {
			background: #000;
		}
		#canvas{
            position: absolute;
			background:#00000000;
		}
		#hacker {
            position: absolute;
			/*display: none;*/
		}
		.operation-btn {
			width: 120px;
			clear: both;
			display: block;
			margin-top: 20px;
		}
	</style>

</head>
<body>
<div style="position: static">
	<div class="row">
		<div class="span12" style="padding: 5px;">
			<div class="btn-group" id="measureGroup">
                <button id="btnArea" class="btn btn-primary">面积</button>
				<button id="btnLength" class="btn">长度</button>
				<button id="btnWidth" class="btn">宽度</button>
                <button id="btnDeep" class="btn">深度</button>
				<button id="btnSave" class="btn">保存</button>
			</div>
		</div>
	</div>
	<div class="row">
		<div class="span9">
			<img id="hacker" src="" />
		</div>
		<div class="span3">
			<div class="input-prepend">
				<span class="add-on">面积（cm2）</span>
				<input id="measure-area" type="text" style="width: auto;">
			</div>
			<div class="input-prepend">
				<span class="add-on">容积（cm3）</span>
				<input id="measure-volume" type="text" style="width: auto;">
			</div>
			<div class="input-prepend">
				<span class="add-on">深度（cm）</span>
				<input id="measure-deep" type="text" style="width: auto;">
			</div>
			<div class="input-prepend">
				<span class="add-on">长度（cm）</span>
				<input id="measure-length" type="text" style="width: auto;">
			</div>
			<div class="input-prepend">
				<span class="add-on">宽度（cm）</span>
				<input id="measure-width" type="text" style="width: auto;">
			</div>
			<div class="input-prepend">
				<span class="add-on">黄色组织（%）</span>
				<input id="measure-yellow" type="text" style="width: auto;">
			</div>
			<div class="input-prepend">
				<span class="add-on">红色组织（%）</span>
				<input id="measure-red" type="text" style="width: auto;">
			</div>
			<div class="input-prepend">
				<span class="add-on">黑色组织（%）</span>
				<input id="measure-black" type="text" style="width: auto;">
			</div>
			<!--
			<button class="btn operation-btn">长度</button>
			<button class="btn operation-btn">宽度</button>
			<button class="btn operation-btn">面积</button>
			<button class="btn operation-btn ">保存</button>
			-->
		</div>
	</div>

</div>

<div id="result"></div>

	<script>
        var index = 0;
		$(function() {
		    configPage();
		});

		function configPage() {
            loadImage(index);
            $('#hacker').load(function () {
                index++;
                setTimeout(loadImage, 100)
            });
		}

		function loadImage() {
            $('#hacker').attr("src","${ctx}/archivesRecord/deepImage?index=" + index +"&"+new Date().getTime());
        }

	</script>
</body>
</html>
