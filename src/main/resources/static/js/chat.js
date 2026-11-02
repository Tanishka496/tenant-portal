const apiUrl = 'http://localhost:8080/api/messages';

function fetchMessages() {
  fetch(apiUrl)
    .then(res => res.json())
    .then(messages => {
      const chat = document.getElementById('chatMessages');
      chat.innerHTML = '';
      messages.forEach(msg => {
        const div = document.createElement('div');
        div.className = 'chat-message';
        div.innerHTML = `<span class="sender">${msg.sender}</span>
          <span class="timestamp">${new Date(msg.timestamp).toLocaleString()}</span>
          <div>${msg.content}</div>`;
        chat.appendChild(div);
      });
      chat.scrollTop = chat.scrollHeight;
    });
}

document.getElementById('deleteLatest').addEventListener('click', function() {
  fetch(apiUrl + '/latest', { method: 'DELETE' })
    .then(response => {
      if (!response.ok) {
        alert('Failed to delete the latest message.');
      }
      fetchMessages();
    })
    .catch(() => alert('Could not connect to the server.'));
});

document.getElementById('chatForm').addEventListener('submit', function(e) {
  e.preventDefault();
  const sender = document.getElementById('sender').value;
  const content = document.getElementById('content').value;
  fetch(apiUrl, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ sender, content })
  }).then(() => {
    document.getElementById('content').value = '';
    fetchMessages();
  });
});

setInterval(fetchMessages, 2000);
fetchMessages();
