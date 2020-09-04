window.onload=function (){

    let url="ws://"+location.host;
    let ws = new WebSocket(url);
    let sendbtn;
    let content;
    let msg=document.getElementById("message");

    let login = document.getElementById("login");

    let tb=document.createElement("table");

    //when click login button
    login.onclick=function () {

        tb.innerHTML=" ";

        //get roomname value
        let roomname=document.getElementById("roomname").value;
        let username=document.getElementById("username").value;

        if(document.getElementsByTagName("button").length ==1) {
            sendbtn = document.createElement("button");
            content = document.createTextNode("Send");
            sendbtn.appendChild(content);
            document.body.appendChild(sendbtn);
        }



        //connect server
        ws.send("join "+roomname);

        let tr=document.createElement("tr");
        msg.value="";
        tb.appendChild(tr);
        document.body.appendChild(tb);

        //get message from server
        ws.onmessage=function (event) {
            let o=JSON.parse(event.data);
            let msgRow=document.createElement("tr");
            let tdUsername=document.createElement("td");
            tdUsername.innerHTML=o.user;
            msgRow.appendChild(tdUsername);

            let tdMsg=document.createElement("td");
            tdMsg.innerHTML=o.message;
            msgRow.appendChild(tdMsg);

            tb.appendChild(msgRow);
        }

        sendbtn.onclick=function () {
            ws.send(username+" "+msg.value);
            msg.value="";

        }
    }

}