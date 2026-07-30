/*
    Allows the right menu to be toggled open and closed (on smaller devices).
*/
const toggleBtn = document.getElementById("right-menu-button");
const rightBar = document.getElementById("right-nav");
/// css class for active button mathcing the current hash
const activeHash = 'active-hash';

toggleBtn.addEventListener("click", function(e) {
    e.preventDefault();
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


// highlighting the right-bar entries as user scrolls
window.addEventListener("load", function () {
    scrollama()
        .setup({
            step:'section[id]',
            offset: 0.3
        })
        .onStepEnter(function (response) {
            const id = response.element.id
            rightBar.querySelectorAll('a.nav-list-link[href="#'+id+'"]').forEach(el => el.classList.add(activeHash))
            history.replaceState(null, '', `#${id}`);
        }).onStepExit(function (response) {
            const id = response.element.id
            rightBar.querySelectorAll('a.nav-list-link[href="#'+id+'"]').forEach(el => el.classList.remove(activeHash))
        })
})