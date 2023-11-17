  
    export async function captureScreen() {
      try {
        const stream = await navigator.mediaDevices.getDisplayMedia({ video: true });
        const videoElement = document.createElement('video');
        videoElement.srcObject = stream;
  
        // Attendez que la vidéo soit chargée pour garantir que la piste vidéo est prête
        await videoElement.play();
  
        const canvas = document.createElement('canvas');
        const context = canvas.getContext('2d');
  
        // Définir la taille du canvas pour correspondre à la vidéo
        canvas.width = videoElement.videoWidth;
        canvas.height = videoElement.videoHeight;
  
        // Dessiner la vidéo sur le canvas
        context.drawImage(videoElement, 0, 0, canvas.width, canvas.height);
  
        // Obtenez les données de l'image sous forme de base64
        const imageDataUrl = canvas.toDataURL('image/png');
  
        // Affichez l'image dans une balise img
        const imgElement = document.getElementById('capturedImage');
        imgElement.src = imageDataUrl;
  
        // Facultatif : arrêtez la vidéo et libérez les ressources
        stream.getTracks().forEach(track => track.stop());
      } catch (error) {
        console.error('Erreur lors de la capture de l\'écran :', error);
      }
    }