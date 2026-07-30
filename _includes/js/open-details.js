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
    let doScroll = true;
    details.forEach(el => {
        if (doScroll) {
            el.scrollIntoView({ behavior: "smooth", block: "start" });
            doScroll = false; // Only scroll once
        }
        console.log(el)
        if (el.tagName.toLowerCase() === "summary") {
            el = el.parentElement;
        }
        if (el.tagName.toLowerCase() === "details") {
            el.setAttribute("open", "true");
        }
    })
}

/**
 * On page load, assigns each details element an ID if it does not have one,
 * and sets up click event listeners on summary elements which changes the url hash.
 */
function changeHashOnClick() {
    document.querySelectorAll("details > summary").forEach(el => {
        const details = el.parentElement;
        if (details.id == null || details.id.trim() === "") {
            // if details element is missing an ID, use the ID from summary element or construct one from summary text
            if (el.id != null && el.id.trim() !== "") {
                details.id = el.id;
            } else {
                // remove any special non alphanumeric characters and replace spaces with hyphens
                details.id = el.textContent.trim().replace(/[^a-zA-Z0-9 ]/g, '').replace(/\s+/g, "-").toLowerCase();
            }
        }
        el.addEventListener("click", function() {
            if (!details.hasAttribute("open")) {
                // adds the hash to the URL but does not scroll since user is already at the element
                history.pushState("", document.title, window.location.pathname + '#' + details.id + window.location.search);
            } else if (details.id === window.location.hash.substring(1)) {
                // removes the hash from the URL
                history.pushState("", document.title, window.location.pathname + window.location.search);
            }
        });
    });
}

document.addEventListener("DOMContentLoaded", changeHashOnClick);
document.addEventListener("DOMContentLoaded", onHashChange);