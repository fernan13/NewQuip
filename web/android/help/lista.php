<html>
    
    <head>
        
        <meta charset="utf-8"/>
    </head>
    
    <body>
        
        <h2>Crear una lista</h2>
        <p> 
            Puedes crear una lista en Quip para realizar un
            seguimiento de tus tareas
        </p>
        
        <h4>Escribir una nota</h4>
        <ol>
            
            <li>Abre la aplicación Quip</li>
            <li>En la parte inferior, toca <strong>Crear lista nueva</strong></li>
            <li>Añade un titulo y elementos a la lista</li>
            <li>Cuando termines, toca Atrás</li>
            
        </ol>
        
        <h4>Añadir elementos completados a una lista</h4>
        <p>
            
            Para añadir un elemento que se ha marcado solo
            debes de desmarcar su casilla de verificacion
            
        </p>
        
        
        <p>
            
            <span>¿Te ha sido útil este artículo?</span>
            
            <form action="#" method="post">
            
                <input type="submit" name="submit" value="Si"/>
                <input type="submit" name="submit" value="No"/>          
            </form>
            
        </p>
        
    </body>
</html>
<?php

    if ( isset ( $_POST['submit']) ) {
        
        echo '<span>Gracias por su respuesta :)</span>';
    }
?>