import 'package:flutter/material.dart';

class PageShell extends StatelessWidget {
  const PageShell({
    super.key,
    required this.title,
    required this.child,
    this.subtitle,
    this.showBackButton = false,
  });

  final String title;
  final String? subtitle;
  final Widget child;
  final bool showBackButton;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        automaticallyImplyLeading: showBackButton,
        title: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(title),
            if (subtitle != null)
              Text(subtitle!, style: Theme.of(context).textTheme.bodySmall),
          ],
        ),
      ),
      body: SafeArea(
        child: Center(
          child: ConstrainedBox(
            constraints: const BoxConstraints(maxWidth: 520),
            child: Padding(padding: const EdgeInsets.all(20), child: child),
          ),
        ),
      ),
    );
  }
}
