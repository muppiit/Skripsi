import 'package:aplikasi_cat/app/app.dart';
import 'package:flutter_test/flutter_test.dart';

void main() {
  testWidgets('menampilkan halaman login CAT', (WidgetTester tester) async {
    await tester.pumpWidget(const CatApp());

    expect(find.text('Masuk CAT'), findsOneWidget);
    expect(find.text('Masuk'), findsOneWidget);
  });
}
