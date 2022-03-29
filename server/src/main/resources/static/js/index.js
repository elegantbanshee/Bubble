var Index = {};

Index.main = function () {
    var request = new XMLHttpRequest();
    request.onreadystatechange = function() {
        if (this.readyState == 4 && this.status == 200) {
            var json = JSON.parse(this.responseText);
            for (var index = 0; index < json.length; index++)
                Index.addImage(json[index].imageId);
        }
    }

    var page = Index.getPage();

    request.open("GET", "/api/image/top?page=PAGE"
        .replace("PAGE", page));
    request.send();

    var next = document.getElementById("next");
    next.addEventListener("click", Index.handleNext);
};

Index.getPage = function () {
    var reg = /https?:\/\/[A-Za-z\.\:\d]*\/(\d*)/;
    var page = reg.exec(window.location.href);
    if (page == null || page[1] === "")
        page = "1";
    else
        page = page[1];
    return page;
};

Index.handleNext = function () {
    var page = Index.getPage()
    page = parseInt(page);
    page++;
    window.location.href = "/PAGE".replace("PAGE", page);
};

Index.addImage = function (id) {
    var a = document.createElement("a");
    a.href = "/i/ID".replace("ID", id);
    var image = document.createElement("img");
    image.src = "https://storage.googleapis.com/bubble_com/bubble/ID.png".replace("ID", id);
    image.className = "image";
    var div = document.getElementById("images");
    a.appendChild(image);
    div.appendChild(a);
};

window.addEventListener("load", Index.main)