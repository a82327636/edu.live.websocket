
function webSocket(socketId,func,params){

    if(typeof(WebSocket) == "undefined") {
        console.log("您的浏览器不支持WebSocket");
    }else{
        socket = new WebSocket("ws://172.16.16.187:8888/webSocket");
        socket.onopen = function() {
            console.log("Socket 已打开");
            var dataString = JSON.stringify(params);
            socket.send(dataString);
        };

        socket.onmessage = function(msg) {
            var str = msg.data;
            console.log(msg);
            if(typeof func=='function'){
                func(msg);
            }
        };

        //关闭事件
        socket.onclose = function() {
            console.log("Socket已关闭");

        };

        //发生了错误事件
        socket.onerror = function() {
            alert("Socket发生了错误");
            //此时可以尝试刷新页面
        }
        return socket;
    }


    function send(message){
        if(!window.WebSocket){return;}
        if(socket.readyState == WebSocket.OPEN){
            socket.send(message);
        }else{
            alert("WebSocket 连接没有建立成功！");
        }
    }


}