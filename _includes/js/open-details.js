// TODO: this definitely needs some tweaks, it does not work as intended
/**
 * Once the page is loaded, it will extract the fragment from the url
 * and if it refers to a {@code details} or {@code summary} element, it will add {@code open} attribute
 * to the relevant {@code details} element.
 */
function onHashChange() {
    const fragment = window.location.hash.substring(1);
    if (fragment == null || fragment.trim() === "") return;
    const details = document.querySelectorAll("#" + fragment);
    if (details == null) return;
    details.forEach(el => {
        if (el.tagName.toLowerCase() === "summary") {
            el = el.parentElement;
        }
        if (el.tagName.toLowerCase() === "details") {
            el.setAttribute("open", "true");
        }
    })
}

document.addEventListener("DOMContentLoaded", onHashChange);
document.addEventListener("hashchange", onHashChange);
// document.addEventListener("click", onHashChange);