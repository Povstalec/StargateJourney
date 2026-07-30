/**
 * Adds ids to the head elements and fixes the links to them
 */
function functionHeadingLink(h) {
    const func = h.querySelector("code").innerText;
    const funcName = func.split("(", 1).at(0);
    const link = h.querySelector(".anchor-heading");
    if(link?.attributes) {
        link.attributes.getNamedItem("href").value = `#${funcName}`;
        link.attributes.getNamedItem("aria-labelledby").value = funcName;
    }
    if(!h.id) {
        h.id = funcName;
    }
}

document.addEventListener("DOMContentLoaded", ()=>
    document.querySelectorAll(".h-function")?.forEach(h => {
        functionHeadingLink(h);
    })
);