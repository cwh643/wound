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
			<img id="hacker" src="${imageUrl}/rgb.jpeg">
			<!--<img id="hacker" src="${ctx}/static/images/hacker.jpg">-->
			<input type="hidden" id="image-uid" value="${uid}" />
			<input type="hidden" id="image-date" value="${date}" />

			<canvas id="canvas" width="600" height="300">
				<span>该浏览器不支持canvas内容</span> <!--对于不支持canvas的浏览器显示-->
			</canvas>
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
        var oGc;
        var oCanvas;

		$(function() {
		    configPage();
		});

		function configPage() {
            var hacker = $("#hacker")[0];
            oCanvas = $("#canvas")[0];
            oGc = oCanvas.getContext( '2d' );
            setTimeout(drawBackground, 1000);


		}

		function drawBackground() {
            oCanvas.width = hacker.width;
            oCanvas.height = hacker.height;
            oGc.drawImage(hacker, 0, 0);
            hacker.display ='none';
            //drawArea();

            $("#btnLength").on('click', onLength);
            $("#btnWidth").on('click', onWidth);
            $("#btnArea").on('click', onArea);
            $("#btnDeep").on('click', onDeep);
            $("#btnSave").on('click', onSave);

            drawArea();
        }

        function defaultLine() {
            //oGc.strokeStyle = 'green';
            //oGc.fillStyle = 'green';//填充颜色
            //oGc.lineWidth = 3;
            oCanvas.onmouseup = function(ev) {
                oCanvas.onmouseup = null;
                var ev = ev || window.event;
                beginX = ev.clientX-oCanvas.offsetLeft;
                beginY = ev.clientY-oCanvas.offsetTop;
                drawCrossPoint(oGc, beginX, beginY, 10);
            }
        }

        function onDeep() {
            selectBtn(this);
            var count = 0;
            var firstX = 0;
            var firstY = 0;
            var restore;
            restore = oGc.getImageData(0, 0, oCanvas.width, oCanvas.height);
            //drawCrossPoint(oGc, beginX, beginY, 10);
            oCanvas.onmousemove = function(ev) {
                var ev = ev || window.event;//获取event对象
                var beginX = ev.clientX-oCanvas.offsetLeft;
                var beginY = ev.clientY-oCanvas.offsetTop;
                oGc.putImageData(restore, 0, 0);
                drawCrossPoint(oGc, beginX, beginY, 10);
            };

            oCanvas.onmousedown = function(ev) {
                var ev = ev || window.event;
                var beginX = ev.clientX-oCanvas.offsetLeft;
                var beginY = ev.clientY-oCanvas.offsetTop;
                drawCrossPoint(oGc, beginX, beginY, 10);
                restore = oGc.getImageData(0, 0, oCanvas.width, oCanvas.height);
                count++;
                if (count == 2) {
                    oCanvas.onmousedown = null;
                    oCanvas.onmousemove = null;
                    //oCanvas.onmouseup = null;
                    var url = '${ctx}/archivesRecord/computerDeep';
                    $.post(url,{
                        uuid: $('#image-uid').val(),//'64a9b96201bb4312b27ef163cbc2f177',
                        date: $('#image-date').val(),//'20180701172557',
                        points: ''+ firstX + ','+ firstY + ','+ beginX + ','+ beginY
                    },function(result){
                        var msg = result.message;
                        if (result.success) {
                            msg = result.length + 'cm';
                            $('#measure-deep').val(result.length);
                        }
                        //drawTips(oGc, endX + 25, endY - 15, 40, 30, msg);
                    }, "json");

				} else {
                    firstX = beginX;
                    firstY = beginY
                }

            };
        }

		function onLength() {
            selectBtn(this);
            oGc.strokeStyle = 'green';
            oGc.fillStyle = 'green';//填充颜色
            oGc.lineWidth = 3;
            drawLine('L');
        }

        function onWidth() {
            selectBtn(this);
            oGc.strokeStyle = 'blue';
            oGc.fillStyle = 'blue';
            oGc.lineWidth = 3;
            drawLine('W');
        }

        function onArea() {
            selectBtn(this);
            oGc.lineWidth = 3;
            drawArea();
        }

        function onSave() {
            selectBtn(this);
            var img = convertCanvasToImage(oCanvas);
            $("#result").append($(img));
        }

        function selectBtn(el) {
            $("#measureGroup button").removeClass('btn-primary');
            $(el).addClass('btn-primary')
		}

		function drawCrossPoint(ctx, x, y, r) {
            //var restore = oGc.getImageData(0, 0, oCanvas.width, oCanvas.height);
            //oGc.putImageData(restore, 0, 0);
            ctx.strokeStyle = '#FFFFFFB2';//画笔颜色
            //ctx.fillStyle = '#FFFFFF80';//填充颜色
            ctx.lineWidth = 2;
            ctx.beginPath();
            ctx.moveTo(x - r, y);
            ctx.lineTo(x + r, y);
            ctx.moveTo(x, y - r);
            ctx.lineTo(x, y + r);
            ctx.stroke();
		}

        function drawLine(type) {
            var beginX = 0, beginY=0, endX = 0, endY=0;
            var isDrag = false;
            var restore;
            oCanvas.onmousedown = function(ev) {
                var ev = ev || window.event;
                beginX = ev.clientX-oCanvas.offsetLeft;
                beginY = ev.clientY-oCanvas.offsetTop;
                isDrag = true;
                restore = oGc.getImageData(0, 0, oCanvas.width, oCanvas.height);
                //console.log("(" + ev.clientX + "," + ev.clientY + "," + oCanvas.offsetLeft + "," + oCanvas.offsetTop + ")");
                //drawPoint(oGc, beginX, beginY); //鼠标在当前画布上X,Y坐标
                //oGc.lineTo(beginX, beginY);
            };
            oCanvas.onmousemove = function(ev) {
                var ev = ev || window.event;//获取event对象
                //oGc.clearRect(0, 0, oCanvas.width, oCanvas.height);
                endX = ev.clientX-oCanvas.offsetLeft;
                endY = ev.clientY-oCanvas.offsetTop;
				if (isDrag) {
                    oGc.putImageData(restore, 0, 0);
                    oGc.beginPath();
                    drawPoint(oGc, beginX, beginY)
                    oGc.moveTo(beginX, beginY);
                    oGc.lineTo(endX, endY);
                    oGc.stroke();
                    drawPoint(oGc, endX, endY);
				}

            };

            oCanvas.onmouseup = function(ev) {
                oCanvas.onmousemove = null;
                oCanvas.onmousedown = null;
                oCanvas.onmouseup = null;
                isDrag = false;
                //drawTips(oGc, endX + 10, endY, endX + 30, endY + 10, '23.3cm');
				var url = '${ctx}/archivesRecord/computerLength';
                $.post(url,{
                    uuid: $('#image-uid').val(),//'64a9b96201bb4312b27ef163cbc2f177',
					date: $('#image-date').val(),//'20180701172557',
					points: ''+ beginX + ','+ beginY + ','+ endX + ','+ endY
				},function(result){
                    var msg = result.message;
					if (result.success) {
                        msg = result.length + 'cm';
                        if ('L' == type) {
                            $('#measure-length').val(result.length);
						}
                        if ('W' == type) {
                            $('#measure-width').val(result.length);
                        }
                    }
                    drawTips(oGc, endX + 25, endY - 15, 40, 30, msg);
                }, "json");

                //var ev = ev || window.event;//获取event对象
                //oGc.lineTo(ev.clientX-oCanvas.offsetLeft,ev.clientY-oCanvas.offsetTop);
                //oGc.stroke();
            };
        }

        function drawPoint(ctx, x, y) {
            //oGc.beginPath();
            //ctx.fillStyle = 'yellow';//填充颜色
            ctx.arc(x,y,5, 0, Math.PI * 2, true);
            ctx.stroke();
            ctx.fill();//填充图形
        }

        function drawTips(ctx, x1, y1, w, h, txt) {
		    var r = h / 2;
		    var lx = x1, ly = y1 + r;
            var rx = x1 + w, ry = y1 + r;
            var cx = x1 + w / 2, cy = y1 + r;
            ctx.strokeStyle = '#FFFFFFB2';//画笔颜色
            ctx.fillStyle = '#FFFFFF80';//填充颜色
            ctx.lineWidth = 1;
            ctx.beginPath();

            ctx.moveTo(x1,y1);
            ctx.lineTo(x1 + w,y1);
            ctx.arc(rx,ry,r, Math.PI * 1.5, Math.PI * 0.5, false);
            ctx.lineTo(x1,y1 + h);
            ctx.arc(lx,ly,r, Math.PI * 0.5, Math.PI * 1.5, false);
            ctx.stroke();
            ctx.fill();//填充图形

            ctx.fillStyle = '#000000';
            ctx.font="12px Georgia";
            var tw = ctx.measureText(txt).width;
            ctx.fillText(txt,cx - tw / 2,cy + 3);
        }

        function drawArea() {
            oGc.beginPath();
            oGc.strokeStyle = '#1BB2B2';//画笔颜色
            var beginX = 0, beginY=0;
            oCanvas.onmousedown = function(ev) {
                var ev = ev || window.event;
                beginX = ev.clientX-oCanvas.offsetLeft;
                beginY = ev.clientY-oCanvas.offsetTop;
                //console.log("(" + ev.clientX + "," + ev.clientY + "," + oCanvas.offsetLeft + "," + oCanvas.offsetTop + ")");
                oGc.moveTo(beginX, beginY); //鼠标在当前画布上X,Y坐标

                document.onmousemove = function(ev) {
                    var ev = ev || window.event;//获取event对象
                    oGc.lineTo(ev.clientX-oCanvas.offsetLeft,ev.clientY-oCanvas.offsetTop);
                    oGc.stroke();
                };
                oCanvas.onmouseup = function() {
                    document.onmousemove = null;
                    oCanvas.onmousedown = null;
                    oCanvas.onmouseup = null;
                    //oGc.lineTo(beginX, beginY);
                    //oGc.stroke();
                    oGc.closePath();//封闭一个图形
                    oGc.stroke();

                    oGc.fillStyle = '#1BB2B1';//填充颜色
                    oGc.fill();//填充图形

                    convertCanvasToImage(oCanvas);
                };
            };


        }

        /* Converts image to canvas; returns new canvas element
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
        }*/

        // Converts canvas to an image
        function convertCanvasToImage(canvas) {
		    var fileType = 'image/jpeg';
            var newImageData = canvas.toDataURL(fileType);   //重新生成图片，<span style="font-family: Arial, Helvetica, sans-serif;">fileType为用户选择的图片类型</span>
            var sendData = newImageData.replace("data:"+fileType+";base64,",'');
            var url = '${ctx}/archivesRecord/computerArea';
            $.post(url,{
                uuid: $('#image-uid').val(),//'64a9b96201bb4312b27ef163cbc2f177',
                date: $('#image-date').val(),//'20180701172557',
                imageData: sendData
            },function(result){
                console.log(result)
                if (result.success) {
                    var areaMap = result.areaMap;
                    $('#measure-area').val(areaMap.area);
                    $('#measure-volume').val(areaMap.volume);
                    $('#measure-deep').val(areaMap.deep);
                    $('#measure-red').val(areaMap.red);
                    $('#measure-yellow').val(areaMap.yellow);
                    $('#measure-black').val(areaMap.black);

                } else {
                    var msg = result.message;
                    alert(msg);
				}
                //drawTips(oGc, endX + 25, endY - 15, 40, 30, msg);
            }, "json");
        }
	</script>
</body>
</html>
