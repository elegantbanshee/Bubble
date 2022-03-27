var Circle =  class {

    constructor(x, y, radius) {
        this.x = x;
        this.y = y;
        this.radius = radius;
    };

    contains(x, y) {
        var distance = Math.sqrt(Math.pow(x - this.x, 2) + Math.pow(y - this.y, 2));
        return distance <= this.radius;
    };
}