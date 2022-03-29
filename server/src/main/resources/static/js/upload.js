var Upload = {};

Upload.main = function () {
    var upload = document.getElementById("upload");

    upload.addEventListener("change", function () {
        Upload.handleUpload(upload.files[0]);
    });
};

Upload.handleUpload = function (file) {
    var upload = document.getElementById("upload");
    upload.style.display = "none";

    var loading = document.createElement("p");
    loading.innerText = "Uploading";
    document.body.appendChild(loading);

    var request = new XMLHttpRequest();
    request.onreadystatechange = function() {
        if (this.readyState == 4 && this.status == 200) {
            var json = JSON.parse(this.responseText);
            window.location = "/edit?id=ID".replace("ID", json.id);
        }
    }
    request.open("PUT", "/api/image/upload");
    request.send(file);
};

window.addEventListener("load", Upload.main)