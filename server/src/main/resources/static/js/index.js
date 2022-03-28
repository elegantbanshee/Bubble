var Index = {};

Index.main = function () {
    var request = new XMLHttpRequest();
    request.onreadystatechange = function() {
        if (this.readyState == 4 && this.status == 200) {
            var json = JSON.parse(this.responseText);
            for (var index = 0; index < json.length; index++)
                Index.addImage(json[index]);
        }
    }
    request.open("GET", "/api/image/top");
    request.send();
};

Index.addImage = function (id) {
    var image = document.createElement("img");
    image.src = "https://storage.googleapis.com/bubble_com/bubble/ID.png".replace("ID", id);
    image.className = "image";
    var div = document.getElementById("images");
    div.appendChild(image);
};

window.addEventListener("load", Index.main)