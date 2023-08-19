function active(){
    document.getElementById("subject-lists-botton-video").disabled = true;
    var header = document.getElementById("subject-lists");
    var btns = header.getElementsByClassName("subject-lists-botton");
    for (var i = 0; i < btns.length; i++) {
        btns[i].addEventListener("click", function() {
            var current = document.getElementsByClassName("active-subject-lists-botton");
            current[0].className = current[0].className.replace(" active-subject-lists-botton", "");
            this.className += " active-subject-lists-botton";
            subjectVisibility();
        });
    }
}

function subjectVisibility() {
    let elem = document.getElementById('content-video'); // получаем элемент по ID
    let style = getComputedStyle(elem); // получаем его стили
    if (style.display === 'none') {
        document.getElementById('content-video').style.display='flex';
        document.getElementById('content-playlist').style.display='none';
        document.getElementById("subject-lists-botton-video").disabled = true;
        document.getElementById("subject-lists-botton-playlist").disabled = false;
      } else if (style.display !== 'none') {
        document.getElementById('content-video').style.display='none';
        document.getElementById('content-playlist').style.display='flex';
        document.getElementById("subject-lists-botton-video").disabled = false;
        document.getElementById("subject-lists-botton-playlist").disabled = true;
    }
}
