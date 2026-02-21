using System.Collections.Concurrent;

/// <summary>
/// In-memory webhook registration store for the api-gateway tier.
/// In production this would be backed by a durable store (e.g. notification-worker's SQLite DB).
/// </summary>
public class WebhookRegistry
{
    public record WebhookEntry(string Id, string Url, IReadOnlyList<string> EventTypes);

    private readonly ConcurrentDictionary<string, WebhookEntry> _webhooks = new();

    public WebhookEntry Register(string url, IReadOnlyList<string> eventTypes)
    {
        var id = Guid.NewGuid().ToString();
        var entry = new WebhookEntry(id, url, eventTypes);
        _webhooks[id] = entry;
        return entry;
    }

    public bool Delete(string id) => _webhooks.TryRemove(id, out _);

    public IEnumerable<WebhookEntry> All() => _webhooks.Values;
}
