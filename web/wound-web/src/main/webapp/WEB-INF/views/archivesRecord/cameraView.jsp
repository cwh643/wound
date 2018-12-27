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
                <input type="hidden" id="hidden-uuid" value="${uuid}" ></input>
                <button id="btnShot" class="btn btn-primary">拍照</button>
			</div>
		</div>
	</div>
	<div class="row">
		<div class="span9">
			<img id="hacker" src="" />
		</div>
	</div>
</div>

<div id="result"></div>

	<script>
        var flag = 1;
		$(function() {
		    configPage();
		});

        window.onbeforeunload = function() {
            if (flag > 0) {
                flag = 0;
            }
        }

		function configPage() {
		    $('#btnShot').on('click', onShotClick);
            $('#hacker').load(function () {
                //console.log('camera load end.')
                if (flag >= 0) {
                    setTimeout(loadImage, 100)
				}
            });
            loadImage();
		}

		function loadImage() {
		    console.log('camera load bengin.')
            $('#hacker').attr("src","${ctx}/archivesRecord/deepImage?flag="+flag+"&"+new Date().getTime());
            if (flag == 0) {
                flag = -1;
            }
        }

        function onShotClick() {
            flag = -1;
            var uuid = $('#hidden-uuid').val();
            window.open("${ctx}/archivesRecord/takePhoto?uuid="+uuid);
        }

	</script>
</body>
</html>
