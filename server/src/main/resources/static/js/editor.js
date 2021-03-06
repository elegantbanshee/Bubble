var Bubble = {};
Bubble.bubbles = [];
Bubble.image = null;

Bubble.main = function () {
    var canvas = document.getElementById("canvas");
    canvas.addEventListener("click", Bubble.handleClick);
    canvas.addEventListener("mousemove", Bubble.handleMouseMove);
    document.body.addEventListener("mouseout", Bubble.handleMouseOut);

    var undo = document.getElementById("undo");
    undo.addEventListener("click", Bubble.handleUndoButton);

    var save = document.getElementById("save");
    save.addEventListener("click", Bubble.handleSave);

    Bubble.createImage();
};

Bubble.handleSave = function () {
    Bubble.drawCanvas(true);

    var canvas = document.getElementById("canvas");

    var image = canvas.toDataURL("image/jpg");

    var request = new XMLHttpRequest();
    request.onreadystatechange = function() {
        if (this.readyState == 4 && this.status == 200) {
            var json = JSON.parse(this.responseText);
            window.location = "/i/ID".replace("ID", json.id);
        }
    }
    request.open("PUT", "/api/image/upload_bubble");
    request.send(image);

    var save = document.getElementById("save");
    document.body.removeChild(save);

    var p = document.createElement("p");
    p.innerText = "Uploading."
    document.body.appendChild(p);
};

Bubble.createImage = function () {
    Bubble.image = new Image();
    Bubble.image.crossOrigin = 'anonymous';

    const params = new URLSearchParams(window.location.search)

    Bubble.image.addEventListener("load", Bubble.handleImageLoad);

    Bubble.image.src = "/api/image/raw/ID"
        .replace("ID", params.get("id"));
};

Bubble.handleImageLoad = function () {
    Bubble.drawCanvas();
};

Bubble.handleUndoButton = function () {
    if (Bubble.bubbles.length > 0) {
        Bubble.bubbles.splice(Bubble.bubbles.length - 1, 1);
        Bubble.drawCanvas();
    }
};

Bubble.handleMouseOut = function () {
    Bubble.drawCanvas();
};

Bubble.handleMouseMove = function (event) {
    var canvas = document.getElementById("canvas");
    var ctx = canvas.getContext("2d");

    var x = event.clientX;
    var y = event.clientY;

    var size = document.getElementById("size")
    var radius = parseInt(size.value);

    Bubble.drawCanvas();

    ctx.beginPath();
    ctx.arc(x, y, radius, 0, 2 * Math.PI, false);
    ctx.stroke();
};

Bubble.handleClick = function (event) {
    var x = event.clientX;
    var y = event.clientY;

    var size = document.getElementById("size")
    var radius = parseInt(size.value);

    Bubble.addBubble(x, y, radius);
    Bubble.drawCanvas();
}

Bubble.addBubble = function (x, y, radius) {
    var bubble = new Circle(x, y, radius);
    Bubble.bubbles.push(bubble);
}

Bubble.drawCanvas = function (finished) {
    var canvas = document.getElementById("canvas");
    canvas.width = 800;
    canvas.height = 800;
    if (Bubble.image) {
        canvas.width = Bubble.image.width
        canvas.height = Bubble.image.height
    }

    var ctx = canvas.getContext("2d");
    ctx.clearRect(0, 0, canvas.width, canvas.height);
    if (Bubble.image)
        ctx.drawImage(Bubble.image, 0, 0);

    var imageData = ctx.getImageData(0, 0, canvas.width, canvas.height);

    for (var x = 0; x < imageData.width; x++) {
        for (var y = 0; y < imageData.height; y++) {
            var index = (y * imageData.width + x) * 4;
            if (Bubble.isCirclePixel(x, y)) {
                //
            }
            else {
                var r = (imageData.data[index] + (255 * .7)) % 255;
                var g = imageData.data[index + 1] - 255 * .7;
                var b = imageData.data[index + 2] - 255 * .7;
                var a = 255;

                if (finished) {
                    var rgb = Bubble.getColor();
                    r = rgb[0];
                    g = rgb[1];
                    b = rgb[2];
                    a = 255;
                }
                imageData.data.set([r, g, b, a], index);
            }
        }
    }

    ctx.putImageData(imageData, 0, 0);
};

Bubble.getColor = function () {
    var r = 0;
    var g = 0;
    var b = 0;
    var input = document.getElementById("color");
    var h = input.value;

    // 3 digits
    if (h.length == 4) {
        r = "0x" + h[1] + h[1];
        g = "0x" + h[2] + h[2];
        b = "0x" + h[3] + h[3];

        // 6 digits
    }
    else if (h.length == 7) {
        r = "0x" + h[1] + h[2];
        g = "0x" + h[3] + h[4];
        b = "0x" + h[5] + h[6];
    }

    return [r, g, b];
};

Bubble.isCirclePixel = function (x, y) {
    for (var bubbleIndex = 0; bubbleIndex < Bubble.bubbles.length; bubbleIndex++) {
        var bubble = Bubble.bubbles[bubbleIndex];
        if (bubble.contains(x, y))
            return true;
    }
    return false;
};


window.addEventListener("load", Bubble.main)