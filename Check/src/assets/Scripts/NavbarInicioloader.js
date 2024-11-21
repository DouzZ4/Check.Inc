fetch('../Components/NavbarPagInicio.html')
    .then(response => response.text())
    .then(data => {
        document.querySelector('#navbar-container').innerHTML = data;
    })
    .catch(error => console.error('Error:', error));
