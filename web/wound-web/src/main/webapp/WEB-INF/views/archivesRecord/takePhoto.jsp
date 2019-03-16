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
		<div class="span8" style="padding: 5px;">
			<div class="btn-group" id="measureGroup">
				<button id="btnArea" class="btn btn-primary">面积</button>
				<button id="btnLength" class="btn">长度</button>
				<button id="btnWidth" class="btn">宽度</button>
				<button id="btnDeep" class="btn">深度</button>
				<button id="btnSave" class="btn">保存</button>
			</div>
		</div>
		<div class="span3 pull-right" style="padding: 5px;">
			<div class="btn-group" id="typeGroup">
				<button id="btnRgb" class="btn btn-primary">3D Photo</button>
				<button id="btnDepth" class="btn">Depth</button>
			</div>
		</div>
	</div>
	<div class="row">
		<div class="span9">
			<img id="hacker" src="${imageUrl}/rgb.jpeg">
			<!--<img id="hacker" src="${ctx}/static/images/hacker.jpg">-->
			<input type="hidden" id="image-uid" value="${uid}" />
			<input type="hidden" id="image-date" value="${date}" />
			<canvas id="canvasImage" width="600" height="300" style="position: absolute">
				<span>该浏览器不支持canvas内容</span> <!--对于不支持canvas的浏览器显示-->
			</canvas>
			<canvas id="canvasArea" width="600" height="300" style="position: absolute">
				<span>该浏览器不支持canvas内容</span> <!--对于不支持canvas的浏览器显示-->
			</canvas>
			<canvas id="canvasWidth" width="600" height="300" style="position: absolute">
				<span>该浏览器不支持canvas内容</span> <!--对于不支持canvas的浏览器显示-->
			</canvas>
			<canvas id="canvasHight" width="600" height="300" style="position: absolute">
				<span>该浏览器不支持canvas内容</span> <!--对于不支持canvas的浏览器显示-->
			</canvas>
			<canvas id="canvasDeep" width="600" height="300" style="position: absolute">
				<span>该浏览器不支持canvas内容</span> <!--对于不支持canvas的浏览器显示-->
			</canvas>
			<canvas id="canvas" width="600" height="300" style="position: absolute">
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
    var oGc, oCanvas, hacker;
    var areaCanvas, widthCanvas, heightCanvas, deepCanvas, imageCanvas;
    var areaLayer, widthLayer, heightLayer, deepLayer, imageLayer;

    $(function() {
        configPage();
    });

    function configPage() {
        hacker = $("#hacker")[0];
        oCanvas = $("#canvas")[0];
        oGc = oCanvas.getContext( '2d' );

        imageLayer = $("#canvasImage")[0];
        imageLayer.width = hacker.width;
        imageLayer.height = hacker.height;
        imageCanvas = imageLayer.getContext('2d');

        widthLayer = $("#canvasWidth")[0];
        widthLayer.width = hacker.width;
        widthLayer.height = hacker.height;
        widthCanvas = widthLayer.getContext('2d');

        heightLayer = $("#canvasHight")[0];
        heightLayer.width = hacker.width;
        heightLayer.height = hacker.height;
        heightCanvas = heightLayer.getContext('2d');

        deepLayer = $("#canvasDeep")[0];
        deepLayer.width = hacker.width;
        deepLayer.height = hacker.height;
        deepCanvas = deepLayer.getContext('2d');

        areaLayer = $("#canvasArea")[0];
        areaLayer.width = hacker.width;
        areaLayer.height = hacker.height;
        areaCanvas = areaLayer.getContext('2d');

        setTimeout(drawBackground, 1000);
    }

    function drawBackground() {
        oCanvas.width = hacker.width;
        oCanvas.height = hacker.height;
        //oGc.drawImage(hacker, 0, 0);
        //hacker.display ='none';
        //drawArea();

        $("#btnLength").on('click', onLength);
        $("#btnWidth").on('click', onWidth);
        $("#btnArea").on('click', onArea);
        $("#btnDeep").on('click', onDeep);
        $("#btnSave").on('click', onSave);
        $("#btnDepth").on('click', onDepth);
        $("#btnRgb").on('click', onRgbPhoto);
        drawArea();
    }


    function onRgbPhoto() {
        selectBtn2(this);
        uuid = $('#image-uid').val();//'64a9b96201bb4312b27ef163cbc2f177'
        date = $('#image-date').val();//'20180701172557',
        $('#hacker').attr("src","${imageUrl}/rgb.jpeg");
    }

    function onDepth() {
        selectBtn2(this);
        uuid = $('#image-uid').val();//'64a9b96201bb4312b27ef163cbc2f177'
        date = $('#image-date').val();//'20180701172557',
        $('#hacker').attr("src","${ctx}/archivesRecord/depthImage?uuid="+uuid+"&date="+date+"&"+new Date().getTime());
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
        restore = deepCanvas.getImageData(0, 0, oCanvas.width, oCanvas.height);
        //drawCrossPoint(oGc, beginX, beginY, 10);
        oCanvas.onmousemove = function(ev) {
            var ev = ev || window.event;//获取event对象
            var beginX = ev.clientX-oCanvas.offsetLeft;
            var beginY = ev.clientY-oCanvas.offsetTop;
            deepCanvas.putImageData(restore, 0, 0);
            drawCrossPoint(deepCanvas, beginX, beginY, 10);
        };

        oCanvas.onmousedown = function(ev) {
            var ev = ev || window.event;
            if (count == 0) {
                deepCanvas.clearRect (0, 0, oCanvas.width, oCanvas.height)
            }
            var beginX = ev.clientX-oCanvas.offsetLeft;
            var beginY = ev.clientY-oCanvas.offsetTop;
            drawCrossPoint(deepCanvas, beginX, beginY, 10);
            restore = deepCanvas.getImageData(0, 0, oCanvas.width, oCanvas.height);
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
        heightCanvas.strokeStyle = '#00FF0080';//'green';
        heightCanvas.fillStyle = '#00FF0080';//填充颜色
        heightCanvas.lineWidth = 2;
        drawLine('L', heightCanvas);
    }

    function onWidth() {
        selectBtn(this);
        widthCanvas.strokeStyle = '#0000FF80';//'blue';
        widthCanvas.fillStyle = '#0000FF80';
        widthCanvas.lineWidth = 2;
        drawLine('W', widthCanvas);
    }

    function onArea() {
        selectBtn(this);
        drawArea();
    }

    function isEmpty(val) {
        return (undefined == val || 0 == val.length)
    }

    function onSave() {
        if (isEmpty($('#measure-area').val())
            || isEmpty($('#measure-volume').val())
            || isEmpty($('#measure-deep').val())
            || isEmpty($('#measure-length').val())
            || isEmpty($('#measure-width').val())
            || isEmpty($('#measure-yellow').val())
            || isEmpty($('#measure-red').val())
            || isEmpty($('#measure-black').val())) {
            alert("请先完成测量");
            return
        }
        selectBtn(this);
        image = convertCanvasToImage(imageCanvas, true);
        postImageData(image);
    }

    function postImageData(image) {
        //var fileType = 'image/jpeg';
        //var newImageData = canvas.toDataURL(fileType);   //重新生成图片，<span style="font-family: Arial, Helvetica, sans-serif;">fileType为用户选择的图片类型</span>
        //var sendData = newImageData.replace("data:"+fileType+";base64,",'');
        var url = '${ctx}/archivesRecord/saveMeasureInfo';
        $.post(url,{
            uuid: $('#image-uid').val(),//'64a9b96201bb4312b27ef163cbc2f177',
            date: $('#image-date').val(),//'20180701172557',
            area: $('#measure-area').val(),
            volume: $('#measure-volume').val(),
            deep: $('#measure-deep').val(),
            length: $('#measure-length').val(),
            width: $('#measure-width').val(),
            yellow: $('#measure-yellow').val(),
            red: $('#measure-red').val(),
            black: $('#measure-black').val(),
            imageData: image
        },function(result){
            console.log(result)
            if (result.success) {
                alert('保存成功');
            } else {
                var msg = result.message;
                alert(msg);
            }
            //drawTips(oGc, endX + 25, endY - 15, 40, 30, msg);
        }, "json");
    }

    function selectBtn(el) {
        $("#measureGroup button").removeClass('btn-primary');
        $(el).addClass('btn-primary')
    }
    function selectBtn2(el) {
        $("#typeGroup button").removeClass('btn-primary');
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

    function drawLine(type, ctx) {
        var beginX = 0, beginY=0, endX = 0, endY=0;
        var isDrag = false;
        var restore;
        oCanvas.onmousedown = function(ev) {
            var ev = ev || window.event;
            beginX = ev.clientX-oCanvas.offsetLeft;
            beginY = ev.clientY-oCanvas.offsetTop;
            isDrag = true;
            ctx.clearRect (0, 0, oCanvas.width, oCanvas.height)
            restore = ctx.getImageData(0, 0, oCanvas.width, oCanvas.height);
            //console.log("(" + ev.clientX + "," + ev.clientY + "," + oCanvas.offsetLeft + "," + oCanvas.offsetTop + ")");
            //drawPoint(ctx, beginX, beginY); //鼠标在当前画布上X,Y坐标
            //ctx.lineTo(beginX, beginY);
        };
        oCanvas.onmousemove = function(ev) {
            var ev = ev || window.event;//获取event对象
            //ctx.clearRect(0, 0, oCanvas.width, oCanvas.height);
            endX = ev.clientX-oCanvas.offsetLeft;
            endY = ev.clientY-oCanvas.offsetTop;
            if (isDrag) {
                ctx.putImageData(restore, 0, 0);
                ctx.beginPath();
                drawPoint(ctx, beginX, beginY)
                ctx.moveTo(beginX, beginY);
                ctx.lineTo(endX, endY);
                ctx.stroke();
                drawPoint(ctx, endX, endY);
            }
        };

        oCanvas.onmouseup = function(ev) {
            oCanvas.onmousemove = null;
            oCanvas.onmousedown = null;
            oCanvas.onmouseup = null;
            isDrag = false;
            //drawTips(ctx, endX + 10, endY, endX + 30, endY + 10, '23.3cm');
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
                drawTips(ctx, endX + 25, endY - 15, 40, 30, msg);
            }, "json");

            //var ev = ev || window.event;//获取event对象
            //ctx.lineTo(ev.clientX-oCanvas.offsetLeft,ev.clientY-oCanvas.offsetTop);
            //ctx.stroke();
        };
    }

    function drawPoint(ctx, x, y) {
        //oGc.beginPath();
        //ctx.fillStyle = 'yellow';//填充颜色
        ctx.beginPath();
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
        areaCanvas.lineWidth = 3;
        //areaCanvas.strokeStyle = '#1BB2B2FF';//画笔颜色
        //areaCanvas.fillStyle = '#1BB2B1FF';//填充颜色
        areaCanvas.strokeStyle = 'rgba(27, 178, 178, 1)';
        areaCanvas.fillStyle = 'rgba(27, 178, 177, 0.7)';
        areaCanvas.beginPath();
        var beginX = 0, beginY=0;
        var minX = 0, minY = 0,maxX = 0, maxY = 0;
        oCanvas.onmousedown = function(ev) {
            var ev = ev || window.event;
            beginX = ev.clientX-oCanvas.offsetLeft;
            beginY = ev.clientY-oCanvas.offsetTop;
            minX = beginX;
			maxX = beginX;
            minY = beginY;
            maxY = beginY;
            //console.log("(" + ev.clientX + "," + ev.clientY + "," + oCanvas.offsetLeft + "," + oCanvas.offsetTop + ")");
            areaCanvas.clearRect(0, 0, oCanvas.width, oCanvas.height);
            areaCanvas.moveTo(beginX, beginY); //鼠标在当前画布上X,Y坐标

            document.onmousemove = function(ev) {
                var ev = ev || window.event;//获取event对象
                moveX = ev.clientX-oCanvas.offsetLeft;
                moveY = ev.clientY-oCanvas.offsetTop;
                if (moveX > maxX) {
                    maxX = moveX;
				}
                if (moveX < minX) {
                    minX = moveX;
                }
                if (moveY > maxY) {
                    maxY = moveY;
                }
                if (moveY < minY) {
                    minY = moveY;
                }
                areaCanvas.lineTo(moveX, moveY);
                areaCanvas.stroke();
            };
            oCanvas.onmouseup = function() {
                document.onmousemove = null;
                oCanvas.onmousedown = null;
                oCanvas.onmouseup = null;
                //oGc.lineTo(beginX, beginY);
                //oGc.stroke();
                areaCanvas.closePath();//封闭一个图形
                areaCanvas.stroke();

                areaCanvas.fill();//填充图形

                image = convertCanvasToImage(imageCanvas, false);
                centerX = minX + (maxX - minX) / 2.0;
                centerY = minY + (maxY - minY) / 2.0;
                reqAreaData(image, areaCanvas, centerX, centerY);
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
    function convertCanvasToImage(canvas, isAll) {
        canvas.save();
        //canvas.globalCompositeOperation="destination-over";
        if (isAll) {
            canvas.drawImage(hacker, 0, 0, oCanvas.width, oCanvas.height);
        }
        canvas.drawImage(areaLayer, 0, 0, oCanvas.width, oCanvas.height);
        if (isAll) {
            canvas.drawImage(widthLayer, 0, 0, oCanvas.width, oCanvas.height);
            canvas.drawImage(heightLayer, 0, 0, oCanvas.width, oCanvas.height);
            canvas.drawImage(deepLayer, 0, 0, oCanvas.width, oCanvas.height);
        }
        canvas.restore();

        //var fileType = 'image/jpeg';
        var fileType = 'image/png';
        var newImageData = imageLayer.toDataURL(fileType);   //重新生成图片，<span style="font-family: Arial, Helvetica, sans-serif;">fileType为用户选择的图片类型</span>
        var sendData = newImageData.replace("data:"+fileType+";base64,",'');
        //debugger
        canvas.clearRect(0, 0, oCanvas.width, oCanvas.height);
        return sendData
    }

    function reqAreaData(image, areaCanvas, centerX, centerY) {
        var url = '${ctx}/archivesRecord/computerArea';
        $.post(url,{
            uuid: $('#image-uid').val(),//'64a9b96201bb4312b27ef163cbc2f177',
            date: $('#image-date').val(),//'20180701172557',
            imageData: image
        },function(result){
            //console.log(result)
			var msg = '';
            if (result.success) {
                var areaMap = result.areaMap;
                $('#measure-area').val(areaMap.area);
                $('#measure-volume').val(areaMap.volume);
                $('#measure-deep').val(areaMap.deep);
                $('#measure-red').val(areaMap.red);
                $('#measure-yellow').val(areaMap.yellow);
                $('#measure-black').val(areaMap.black);
                msg = "area:" + areaMap.area + "cm²";
            } else {
                msg = result.message;
                //alert(msg);
            }
            drawTips(areaCanvas, centerX - 50, centerY - 15, 80, 30, msg);
            //drawTips(oGc, endX + 25, endY - 15, 40, 30, msg);
        }, "json");
    }
</script>
</body>
</html>
