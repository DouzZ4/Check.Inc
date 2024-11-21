    async function loadNavbar() {
        try {
            const response = await fetch('../Components/NavbarPagInicio.html');
            if (!response.ok) throw new Error('Error al cargar la barra de navegación.');
            const navbarHTML = await response.text();
            document.getElementById('navbar-container').innerHTML = navbarHTML;
        } catch (error) {
            console.error('Error:', error);
        }
    }

    loadNavbar();