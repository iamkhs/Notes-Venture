let activePopup = null; // Store the currently active popup

// Function to show the create note popup
function showPopup() {
    document.querySelector("#title").value = "";
    document.querySelector("#description").value = "";
    document.querySelector("#create-note-popup").style.display = "flex";
    activePopup = "create-note-popup";
}

function showUpdatePopup(noteLink) {
    const noteId = noteLink.getAttribute("data-noteid");
    const title = noteLink.getAttribute("data-title");
    const description = noteLink.getAttribute("data-description");
    document.querySelector("#note-id").value = noteId;
    document.querySelector("#update-title").value = title;

    const updateDescriptionElement = document.querySelector("#update-description");
    updateDescriptionElement.innerHTML = description;

    // Check if the TinyMCE editor is already initialized
    const updateDescriptionEditor = tinymce.get('update-description');

    if (!updateDescriptionEditor) {
        // If the editor is not initialized, initialize it
        tinymce.init({
            selector: '#update-description',
            skin: "oxide-dark",
            content_css: "dark",

            height: 400, // Change this height as per your requirement
            // autoresize_bottom_margin: 20,
        });
    } else {
        // If the editor is already initialized, set its content
        updateDescriptionEditor.setContent(description);
    }

    // Set the updated description to the hidden textarea
    document.querySelector("#update-description-hidden").value = description;

    document.querySelector("#update-note-popup").style.display = "flex";
    activePopup = "update-note-popup";
}


// Function to update the hidden textarea with the updated description from TinyMCE editor
function updateHiddenDescription() {
    // Trigger the save operation on the TinyMCE editor to update its content
    tinymce.triggerSave();

    // Get the updated description from the TinyMCE editor
    const updateDescriptionEditor = tinymce.get('update-description');
    const updatedDescription = updateDescriptionEditor.getContent();

    // Set the updated description to the hidden textarea
    document.querySelector("#update-description-hidden").value = updatedDescription;
}

// Function to submit the form after updating the hidden description field
function submitForm() {
    updateHiddenDescription(); // Update the hidden description field with the latest content from TinyMCE
    document.querySelector("#update-form").submit();
}


// Function to hide the create note popup
function hidePopup() {
    document.querySelector("#create-note-popup").style.display = "none";
    activePopup = null;
}

// Function to hide the update note popup
function hideUpdatePopup() {
    document.querySelector("#update-note-popup").style.display = "none";
    activePopup = null;
}

// Function to hide the active popup if there is one
function hideActivePopup() {
    if (activePopup === "create-note-popup") {
        hidePopup();
    } else if (activePopup === "update-note-popup") {
        hideUpdatePopup();
    }
}

// Add event listener to the "create-note" button
document.querySelector("#create-note").addEventListener("click", function (event) {
    event.preventDefault(); // Prevent default link behavior
    hideActivePopup(); // Close any active popup before showing the create note popup
    showPopup();
});

// Add event listener to all note cards to open the update note popup
const noteLinks = document.querySelectorAll(".note-link");
noteLinks.forEach((noteLink) => {
    noteLink.addEventListener("click", function (event) {
        event.preventDefault();
        hideActivePopup(); // Close any active popup before showing the update note popup
        showUpdatePopup(noteLink);
    });
});

// Add event listener to the popup container to close when clicked outside the card
document.querySelector(".popup-container").addEventListener("click", function (event) {
    if (event.target === this) {
        hideActivePopup();
    }
});

document.querySelector("#update-note-popup").addEventListener("click", function (event) {
    if (event.target === this) {
        hideUpdatePopup();
    }
});

// Add event listener to the close button of create note popup
document.querySelector("#create-note-popup .popup-close").addEventListener("click", function () {
    hidePopup();
});

// Add event listener to the close button of update note popup
document.querySelector("#update-note-popup .popup-close").addEventListener("click", function () {
    hideUpdatePopup();
});
