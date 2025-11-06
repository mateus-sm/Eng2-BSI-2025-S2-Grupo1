document.getElementById('foto').addEventListener('change', function(event) {
    const file = event.target.files[0];
    const previewId = 'image-preview';
    let preview = document.getElementById(previewId);

    // Se ainda não existe o <img>, cria dinamicamente
    if (!preview) {
        preview = document.createElement('img');
        preview.id = previewId;
        preview.style.maxWidth = '200px';
        preview.style.display = 'none';
        preview.className = 'mt-3 border rounded';
        event.target.parentNode.appendChild(preview);
    }

    if (!file) {
        preview.style.display = 'none';
        return;
    }

    const reader = new FileReader();
    reader.onload = function(e) {
        preview.src = e.target.result;
        preview.style.display = 'block';
    };
    reader.readAsDataURL(file);
});

document.getElementById('upload-form').addEventListener('submit', async (e) => {
    e.preventDefault();

    const idAtividade = document.getElementById('id_atividade').value;
    const idMembro = document.getElementById('id_membro').value;
    const fotoInput = document.getElementById('foto');

    if (fotoInput.files.length === 0) {
        alert("Por favor, selecione um arquivo de foto.");
        return;
    }
    if (!idAtividade || !idMembro) {
        alert("ID da Atividade e ID do Membro são obrigatórios.");
        return;
    }

    const formData = new FormData();
    formData.append('id_membro', idMembro);
    formData.append('foto', fotoInput.files[0]); // Pega o arquivo

    const API_URL = `/apis/atividade/${idAtividade}/fotos`;

    try {
        const response = await fetch(API_URL, {
            method: 'POST',
            body: formData
        });

        if (!response.ok) {
            let errorMsg = 'Falha no upload';
            try {
                const erro = await response.json();
                errorMsg = erro.erro || erro.message || 'Falha no upload';
            } catch(e) {
                errorMsg = `Falha no upload: ${response.statusText}`;
            }
            throw new Error(errorMsg);
        }

        const resultado = await response.json();
        alert('Foto enviada com sucesso! ID da Foto: ' + resultado.id);

    } catch (error) {
        console.error('Erro:', error);
        alert('Erro: ' + error.message);
    }
});

document.getElementById('foto').addEventListener('change', function(event) {
    const file = event.target.files[0];
    const preview = document.getElementById('image-preview');

    if (!file) {
        preview.style.display = 'none';
        return;
    }

    const reader = new FileReader();
    reader.onload = function(e) {
        preview.src = e.target.result;
        preview.style.display = 'block';
    };
    reader.readAsDataURL(file);
});
