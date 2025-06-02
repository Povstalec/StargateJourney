/**
 * Creates minecraft style tooltips
 *
 * Replaces normal tooltips. Supports minecraft [[formatting codes]] (except k), and a description with line breaks (/).
 *
 * <pre>
 * data-minetip-title="&6Tooltip Title"
 * data-minetip-text="Tooltip text/With a new line"
 * </pre>
 *
 * Licensed under CC BY-NC-SA 3.0<br>
 * https://minecraft.wiki/<br>
 * Rewritten to plain JS without JQuery
 */

const escapeChars = { '\\&': '&#38;', '<': '&#60;', '>': '&#62;' };
const escape = function( text ) {
    // "\" must be escaped first
    return text.replace( /\\\\/g, '&#92;' )
        .replace( /\\&|[<>]/g, function( char ) { return escapeChars[char]; } );
};
let tooltip = null;
let winWidth, winHeight, width, height;

const elements = document.querySelectorAll( '[data-minetip-title], [data-minetip-text]' );

newEventListener = (ev, cb) => elements.forEach(e => e.addEventListener(ev, cb));

newEventListener('mouseover', function( e ) {
    tooltip?.remove();
    const title = this.getAttribute("data-minetip-title");
    // No title or title only contains formatting codes
    if (title == null || title === '' || title.toString().trim() === '' || title.replace( /&([0-9a-fl-or])/g, '' ) === '') {
        return;
    }

    let content = '<span class="minetip-title">' + escape( title ) + '&r</span>';
    const description = this.getAttribute("data-minetip-text")?.trim();
    if ( description ) {
        // Apply normal escaping plus "/"
        const escapedDescription = escape( description ).replace( /\\\//g, '&#47;' );
        content += '<span class="minetip-description">' + escapedDescription.replace( /\//g, '<br>' ) + '&r</span>';
    }
    // Add classes for minecraft formatting codes
    while ( content.search( /&[0-9a-fl-o]/ ) > -1 ) {
        content = content.replace( /&([0-9a-fl-o])(.*?)(&r|$)/g, '<span class="minetip-format-$1">$2</span>&r' );
    }
    // Remove reset formatting
    content = content.replace( /&r/g, '' );

    tooltip = document.createElement( 'div' );
    tooltip.id = 'minetip-tooltip';
    tooltip.innerHTML = content;
    document.body.appendChild(tooltip);

    winWidth = window.innerWidth
    winHeight = window.innerHeight;
    width = tooltip.offsetWidth;
    height = tooltip.offsetHeight;

    this.dispatchEvent(new CustomEvent('mousemove', e));

});

newEventListener('mousemove', function( e ) {
    if (!tooltip) {
        return;
    }

    // Get mouse position and add default offsets
    var top = e.clientY - 34;
    var left = e.clientX + 14;

    // If going off the right of the screen, go to the left of the cursor
    if ( left + width > winWidth ) {
        left -= width + 36;
    }

    // If now going off to the left of the screen, resort to going above the cursor
    if ( left < 0 ) {
        left = 0;
        top -= height - 22;

        // Go below the cursor if too high
        if ( top < 0 ) {
            top += height + 47;
        }
        // Don't go off the top of the screen
    } else if ( top < 0 ) {
        top = 0;
        // Don't go off the bottom of the screen
    } else if ( top + height > winHeight ) {
        top = winHeight - height;
    }

    // Apply the positions
    tooltip.style.top = top + 'px';
    tooltip.style.left = left + 'px';
});

newEventListener('mouseleave', function() {
    if ( tooltip ) {
        tooltip.remove();
        tooltip = null;
    }
});

