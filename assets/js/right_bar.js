/*
    Allows the right menu to be toggled open and closed (on smaller devices).
*/
const toggleBtn = document.getElementById("right-menu-button");
const rightBar = document.getElementById("right-nav");

toggleBtn.addEventListener("click", function(e) {
    e.preventDefault();
    console.log(toggleBtn, toggleBtn.parentElement)
    if(toggleBtn.classList.toggle('nav-open')) {
        rightBar.classList.add('nav-open');
        toggleBtn.parentElement.classList.add('nav-open');
        toggleBtn.ariaPressed = true;
    } else {
        rightBar.classList.remove('nav-open');
        toggleBtn.parentElement.classList.remove('nav-open');
        toggleBtn.ariaPressed = false;
    }
});
