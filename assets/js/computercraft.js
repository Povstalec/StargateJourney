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

/**
 * Moves labels that are right after the function heading into the heading element
 */
function headingLabels(h) {
    const wrapper = document.createElement("span");
    const funcName = h.querySelector("code");
    funcName.remove();
    wrapper.appendChild(funcName);
    while (h.nextElementSibling && h.nextElementSibling.tagName === "P" && h.nextElementSibling.classList.contains("label")) {
        const label = h.nextElementSibling;
        label.remove();
        wrapper.appendChild(label);
    }
    h.prepend(wrapper);
}

document.addEventListener("DOMContentLoaded", ()=>
    document.querySelectorAll(".h-function")?.forEach(h => {
        headingLabels(h);
        functionHeadingLink(h);
    })
);