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
			background:white;
		}
		#hacker {
			display: none;
		}
	</style>

</head>
<body>
<div style="position: static">
	<canvas id="canvas" width="600" height="300">
		<span>该浏览器不支持canvas内容</span> <!--对于不支持canvas的浏览器显示-->
	</canvas>
	<img id="hacker" src="${ctx}/static/images/hacker.jpg">
</div>
<button id="save">保存</button>
<div id="result"></div>

	<script>
        window.onload =function() {
            //alert('aa');
            var hacker = document.querySelector( "#hacker" );
            var oCanvas = document.querySelector( "#canvas" ),
                oGc = oCanvas.getContext( '2d' );

            ///*
            var save = document.querySelector( "#save" );
            save.onclick = function () {
                var img = convertCanvasToImage(oCanvas);
                document.querySelector( "#result" ).appendChild(img);
        	};
        	//*/

			oCanvas.width = hacker.width;
            oCanvas.height = hacker.height;
            oGc.drawImage(hacker, 0, 0);
            hacker.display ='none';

/*
            oGc.beginPath();
            oGc.strokeStyle = 'red';
            oGc.moveTo( 50, 50 );
            oGc.lineTo( 250, 50 );
            oGc.lineTo( 250, 150 );
            oGc.lineTo( 50, 50 );
            oGc.stroke();
*/

            oGc.beginPath();
            oGc.strokeStyle = 'yellow';
            var beginX = 0, beginY=0;
            oCanvas.onmousedown = function(ev) {
                var ev = ev || window.event;
                beginX = ev.clientX-oCanvas.offsetLeft;
                beginY = ev.clientY-oCanvas.offsetTop;
                console.log("(" + ev.clientX + "," + ev.clientY + "," + oCanvas.offsetLeft + "," + oCanvas.offsetTop + ")");
                oGc.moveTo(beginX, beginY); //鼠标在当前画布上X,Y坐标

                document.onmousemove = function(ev) {
                    var ev = ev || window.event;//获取event对象
                    oGc.lineTo(ev.clientX-oCanvas.offsetLeft,ev.clientY-oCanvas.offsetTop);
                    oGc.stroke();
                };
                oCanvas.onmouseup = function() {
                    document.onmousemove = null;
                    document.onmouseup = null;
                    //oGc.lineTo(beginX, beginY);
                    //oGc.stroke();
                    oGc.closePath();//封闭一个图形
                    oGc.stroke();

                    oGc.fillStyle = 'yellow';
                    oGc.fill();//填充图形
                };
            };


        }

        // Converts image to canvas; returns new canvas element
        function convertImageToCanvas(image) {
            var canvas = document.createElement("canvas");
            canvas.width = image.width;
            canvas.height = image.height;
            canvas.getContext("2d").drawImage(image, 0, 0);

            return canvas;
        }

        // Converts canvas to an image
        function convertCanvasToImage(canvas) {
            var image = new Image();
            image.src = canvas.toDataURL("image/png");
            return image;
        }

        // Converts canvas to an image
        function convertCanvasToImage(canvas) {
            var newImageData = cvs.toDataURL(fileType, 0.8);   //重新生成图片，<span style="font-family: Arial, Helvetica, sans-serif;">fileType为用户选择的图片类型</span>
            var sendData = newImageData.replace("data:"+fileType+";base64,",'');
            $.post('/user/personalchange',{type:'photo',value:sendData},function(data){
                if(data.code == '200'){
                    $('.modify_img').attr('src',newImageData);
                    $.notify.close();
                }else{
                    $.notify.show(data.message, {placement: 'center'});
                }
            });
        }
	</script>
</body>
</html>
